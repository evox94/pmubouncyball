package ds130626.pmu.rti.etf.rs.accelgame.android.build;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import ds130626.pmu.rti.etf.rs.accelgame.android.PixelConverter;
import ds130626.pmu.rti.etf.rs.accelgame.model.Hole;
import ds130626.pmu.rti.etf.rs.accelgame.model.Level;

/**
 * Created by smiljan on 2/12/17.
 */

public class HoleWrapper extends BuilderModel.Object {
    Hole hole;
    private int colorPrimary;
    private int colorSecondary;
    private boolean isGoal;
    private Undoable undoable;

    public HoleWrapper(Level.Builder builder, Hole hole, int colorPrimary, int colorSecondary, boolean isGoal) {
        super(builder);
        this.hole = hole;
        this.colorPrimary = colorPrimary;
        this.colorSecondary = colorSecondary;
        this.isGoal = isGoal;
    }

    @Override
    public void move(float x, float y) {
        hole.setX(x);
        hole.setY(y);
        if(isGoal){
            setValid(builder.canPlaceGoal(hole));
        }else{
            setValid(builder.canPlaceTrap(hole));
        }
    }

    @Override
    public int save() {
        if(isGoal){
            if(builder.canPlaceGoal(hole)){
                Hole p  = builder.create().getGoal();
                final Hole old;
                if(p!=null){
                    old = new Hole(p.getX(), p.getY(), p.getR());
                }else{
                    old = null;
                }
                undoable = new Undoable() {
                    @Override
                    public void undo() {
                        builder.setGoal(old);
                    }
                };
                return builder.setGoal(hole);
            }
        }else {
            if (builder.canPlaceTrap(hole)) {
                final int id = builder.addTrap(hole);
                undoable = new Undoable() {
                    @Override
                    public void undo() {
                        builder.removeTrap(id);
                    }
                };
                return id;
            }
        }
        return -1;
    }

    @Override
    public void draw(Canvas c, Paint p, PixelConverter converter) {
        p.reset();
        float x = converter.pxFromDp(hole.getX());
        float y = converter.pxFromDp(hole.getY());
        if(isValid()){
            p.setColor(colorSecondary);
            c.drawCircle(x, y, converter.pxFromDp(hole.getR())*1.2f, p);
            p.setColor(colorPrimary);
            c.drawCircle(x, y, converter.pxFromDp(hole.getR()), p);
        }else{
            p.setColor(BuilderModel.Constants.DEFAULT_INVALID_COLOR);
            c.drawCircle(x, y, converter.pxFromDp(hole.getR())*1.2f, p);
        }
    }

    @Override
    public Map<String, Object> getViewProperties() {
        Map<String, Object> props = new Hashtable<>();
        props.put(BuilderModel.Constants.KEY_COLOR_PRIMARY, colorPrimary);
        props.put(BuilderModel.Constants.KEY_COLOR_SECONDARY, colorSecondary);
        return props;
    }

    @Override
    public Undoable getUndoAction() {
        return undoable;
    }
}
