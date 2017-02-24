package ds130626.pmu.rti.etf.rs.accelgame.android.build;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.Toast;

import java.net.InterfaceAddress;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;

import ds130626.pmu.rti.etf.rs.accelgame.android.CanvasWrapper;
import ds130626.pmu.rti.etf.rs.accelgame.android.LevelLoader;
import ds130626.pmu.rti.etf.rs.accelgame.android.PixelConverter;
import ds130626.pmu.rti.etf.rs.accelgame.model.GameInstance;
import ds130626.pmu.rti.etf.rs.accelgame.model.Hole;
import ds130626.pmu.rti.etf.rs.accelgame.model.Level;
import ds130626.pmu.rti.etf.rs.accelgame.model.Obstacle;
import ds130626.pmu.rti.etf.rs.accelgame.view.View;

/**
 * Created by smiljan on 2/11/17.
 */

public class BuilderModel {
    private Level.Builder builder;
    private Map<Integer, Map<String, java.lang.Object>> levelViewProperties;
    private Object currentObject;
    private View view;
    private NotificationListener notificationListener;
    private boolean saved = true;
    private Stack<Undoable> undoStack;
    public BuilderModel(float width , float height, View view, NotificationListener notificationListener){
        builder = Level.createBuilder();
        builder.setWidth(width);
        builder.setHeight(height);
        undoStack = new Stack<>();
        this.notificationListener = notificationListener;
        levelViewProperties = new Hashtable<>();
        initBackground();
        this.view = view;
    }

    private void initBackground() {
        Hashtable<String, java.lang.Object> backgroundProps = new Hashtable<>();
        backgroundProps.put(Constants.KEY_COLOR_PRIMARY, Constants.DEFAULT_BACKGROUND_COLOR);
        levelViewProperties.put(Constants.KEY_BACKGROUND, backgroundProps);
    }

    public Map<Integer, Map<String, java.lang.Object>> getLevelViewProperties() {
        return levelViewProperties;
    }

    public void placeBall(float x, float y, float r, int color){
        Level.BallParams params = new Level.BallParams(x,y,r, GameInstance.Constants.DEFAULT_BALL_MASS);
        if(builder.canPlaceBall(params)){
            currentObject = new BallWrapper(builder, params, color);
        }
        view.refresh();
    }

    public void placeGoal(float x, float y, float r, int colorInside, int borderColor){
        Hole h = new Hole(x,y,r);
        if(builder.canPlaceGoal(h)){
            currentObject = new HoleWrapper(builder,h,colorInside,borderColor,true);
        }
        view.refresh();
    }

    public void placeObstacle(float x, float y, int color){
        currentObject = new ObstacleWrapper(builder,x,y,color);
        view.refresh();
    }

    public void placeTrap(float x, float y, float r, int colorInside, int borderColor){
        Hole h = new Hole(x,y,r);
        if(builder.canPlaceTrap(h)){
            currentObject = new HoleWrapper(builder,h,colorInside,borderColor,false);
        }
        view.refresh();
    }

    public void setBackground(int color){
        levelViewProperties.get(Constants.KEY_BACKGROUND).put(Constants.KEY_COLOR_PRIMARY, color);
        saved = false;
        view.refresh();
    }

    public void redraw(CanvasWrapper canvas){
        Level level = builder.create();

        canvas.drawBackground(level);

        if(level.getGoal()!=null){
            canvas.drawGoal(level.getGoal());
        }
        for(Hole h: level.getTraps()){
            canvas.drawTrap(h);
        }
        for(Obstacle o: level.getObstacles()){
            canvas.drawObstacle(o);
        }
        if(level.getBallParams()!=null){
            canvas.drawBallParams(level.getBallParams());
        }
        if(currentObject != null){
            currentObject.draw(canvas.getCanvas(), canvas.getPaint(), canvas.getPixelConverter());
        }
    }

    public void move(float x, float y){
        if(currentObject!=null){
            currentObject.move(x,y);
            view.refresh();
        }
    }

    public void save(){
        if(currentObject!=null){
            final int id = currentObject.save();
            if(id!=-1){
                final Undoable objUndoable = currentObject.getUndoAction();
                Undoable wrapper;
                if(levelViewProperties.containsKey(id)){
                    final Map<String, java.lang.Object> old = new Hashtable<>(levelViewProperties.get(id));
                    wrapper = new Undoable() {
                        @Override
                        public void undo() {
                            objUndoable.undo();
                            levelViewProperties.put(id, old);
                        }
                    };
                }else{
                    wrapper = new Undoable() {
                        @Override
                        public void undo() {
                            objUndoable.undo();
                            levelViewProperties.remove(id);
                        }
                    };
                }
                levelViewProperties.put(id, currentObject.getViewProperties());
                saved = false;
                undoStack.push(wrapper);
            }
            currentObject = null;
            view.refresh();
        }

    }
    public void reset(){
        builder.resetObjects();
        levelViewProperties.clear();
        undoStack.clear();
        initBackground();
        view.refresh();
        notificationListener.onMessage("Cleared!");
    }

    public void undo(){
        try{
            undoStack.pop().undo();
            view.refresh();
            saved = false;
            notificationListener.onMessage("Undo!");
        }catch (EmptyStackException ex){
            notificationListener.onMessage("Nothing to undo");
        }
    }

    public Level.Builder getBuilder(){
        return builder;
    }

    public void saveLevel(String name, LevelLoader loader, Bitmap bitmap, float density){
        builder.setName(name);
        saveLevel(loader, bitmap, density);
    }

    public void saveLevel(LevelLoader loader, Bitmap bitmap, float density){
        LevelWrapper levelWrapper = new LevelWrapper(builder.create(), levelViewProperties, bitmap, density);
        loader.save(levelWrapper);
        levelWrapper.getBitmap().recycle();
        notificationListener.onMessage("Saved!");
        saved = true;
    }

    static abstract class Object{
        private boolean valid;
        Level.Builder builder;
        public Object(Level.Builder builder){
            this.builder = builder;
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        public abstract void move(float x, float y);

        public abstract int save();

        public abstract void draw(Canvas c, Paint p, PixelConverter converter);

        public abstract Map<String, java.lang.Object> getViewProperties();

        public abstract Undoable getUndoAction();
    }

    static public class Constants{
        public static final int DEFAULT_BALL_COLOR = Color.BLACK;
        public static final int DEFAULT_BALL_COLOR_PROGRESS = 0;
        public static final int DEFAULT_INVALID_COLOR = 0xBBFF0000;
        public static final int DEFAULT_HOLE_COLOR = Color.BLACK;
        public static final int DEFAULT_HOLE_COLOR_PROGRESS = 0;
        public static final int DEFAULT_GOAL_SECONDARY = Color.GREEN;
        public static final int DEFAULT_GOAL_SECONDARY_PROGRESS = 512;
        public static final int DEFAULT_TRAP_SECONDARY = Color.RED;
        public static final int DEFAULT_TRAP_SECONDARY_PROGRESS = 1024;
        public static final int DEFAULT_GOAL_RADIUS = 10;
        public static final int DEFAULT_OBSTACLE_COLOR = Color.BLACK;
        public static final int DEFAULT_OBSTACLE_COLOR_PROGRESS = 0;
        public static final int DEFAULT_BACKGROUND_COLOR = Color.WHITE;
        public static final int DEFAULT_BACKGROUND_COLOR_PROGRESS = 256*7-1;

        public static final String KEY_COLOR_PRIMARY = "color_primary";
        public static final String KEY_COLOR_SECONDARY = "color_secondary";
        public static int KEY_BACKGROUND = 0;
    }

    public interface NotificationListener{
        void onMessage(String message);
    }

    public boolean isSaved() {
        return saved;
    }
}
