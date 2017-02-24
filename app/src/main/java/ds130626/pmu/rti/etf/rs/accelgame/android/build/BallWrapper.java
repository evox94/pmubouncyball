package ds130626.pmu.rti.etf.rs.accelgame.android.build;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import ds130626.pmu.rti.etf.rs.accelgame.android.PixelConverter;
import ds130626.pmu.rti.etf.rs.accelgame.model.Level;

/**
 * Created by smiljan on 2/11/17.
 */

public class BallWrapper extends BuilderModel.Object {
    private Level.BallParams ballParams;
    private int color;
    private Undoable undoable;

    public BallWrapper(Level.Builder builder, Level.BallParams ballParams, int color) {
        super(builder);
        this.ballParams = ballParams;
        this.color = color;
    }

    @Override
    public void move(float x, float y) {
        ballParams.setStartX(x);
        ballParams.setStartY(y);
        setValid(builder.canPlaceBall(ballParams));
    }

    @Override
    public int save() {
        if(builder.canPlaceBall(ballParams)){
            Level.BallParams p  = builder.create().getBallParams();
            final Level.BallParams old;
            if(p!=null){
                old = new Level.BallParams(p.getStartX(), p.getStartY(), p.getR(), p.getMass());
            }else{
                old = null;
            }
            undoable = new Undoable() {
                @Override
                public void undo() {
                    builder.setBall(old);
                }
            };
            builder.setBall(ballParams);
            return ballParams.getId();
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

        c.drawCircle(converter.pxFromDp(
                ballParams.getStartX()),
                converter.pxFromDp(ballParams.getStartY()),
                converter.pxFromDp(ballParams.getR()),p);
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
