package ds130626.pmu.rti.etf.rs.accelgame.android.build;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import ds130626.pmu.rti.etf.rs.accelgame.android.PixelConverter;
import ds130626.pmu.rti.etf.rs.accelgame.model.Level;
import ds130626.pmu.rti.etf.rs.accelgame.model.Obstacle;

/**
 * Created by smiljan on 2/12/17.
 */

public class ObstacleWrapper extends BuilderModel.Object {
    private RectF helper;
    private int color;
    private float startX;
    private float startY;
    Obstacle o;
    private Undoable undoable;

    public ObstacleWrapper(Level.Builder builder, float x, float y, int color) {
        super(builder);
        this.color = color;
        o = new Obstacle(x,y,0,0);
        helper = new RectF();
        helper.left = x;
        helper.top = y;
        helper.right = x;
        helper.bottom = y;
        startX = x;
        startY = y;
    }

    private void fillObstacle(){
        float width = helper.right - helper.left;
        float height = helper.bottom - helper.top;
        o.setX(helper.left + width/2f);
        o.setY(helper.top+height/2f);
        o.setWidth(width);
        o.setHeight(height);
    }

    @Override
    public void move(float x, float y) {
        if(x>startX){
            helper.right = x;
        }else{
            helper.left = x;
        }
        if(y>startY){
            helper.bottom = y;
        }else{
            helper.top = y;
        }

        //normalize();
        fillObstacle();
        setValid(builder.canPlaceObstacle(o));
    }

    private void normalize() {
        if(helper.left > helper.right){
            float temp = helper.left;
            helper.left = helper.right;
            helper.right = temp;
        }
        if(helper.top > helper.bottom) {
            float temp = helper.top;
            helper.top = helper.bottom;
            helper.bottom = temp;
        }
    }

    @Override
    public int save() {
        if(builder.canPlaceObstacle(o) && o.getHeight()>0 && o.getWidth()>0){
            final int id = builder.addObstacle(o);
            undoable = new Undoable() {
                @Override
                public void undo() {
                    builder.removeObstacle(id);
                }
            };
            return id;
        }
        return -1;
    }

    @Override
    public void draw(Canvas c, Paint p, PixelConverter converter) {
        p.reset();
        if(isValid()){
            p.setColor(color);
        }else{
            p.setColor(BuilderModel.Constants.DEFAULT_INVALID_COLOR);
        }
        c.drawRect(converter.pxFromDp(helper.left),converter.pxFromDp(helper.top),converter.pxFromDp(helper.right),converter.pxFromDp(helper.bottom),p);
    }

    @Override
    public Map<String, Object> getViewProperties() {
        Map<String, Object> props = new Hashtable<>();
        props.put(BuilderModel.Constants.KEY_COLOR_PRIMARY, color);
        return props;
    }

    @Override
    public Undoable getUndoAction() {
        return undoable;
    }
}
