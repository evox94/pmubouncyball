package ds130626.pmu.rti.etf.rs.accelgame.android;


import android.graphics.Color;
import android.graphics.Paint;

import java.util.Map;

import ds130626.pmu.rti.etf.rs.accelgame.android.build.BuilderModel;
import ds130626.pmu.rti.etf.rs.accelgame.model.Ball;
import ds130626.pmu.rti.etf.rs.accelgame.model.Hole;
import ds130626.pmu.rti.etf.rs.accelgame.model.Level;
import ds130626.pmu.rti.etf.rs.accelgame.model.Obstacle;
import ds130626.pmu.rti.etf.rs.accelgame.view.Canvas;

/**
 * Created by smiljan on 1/31/17.
 */

public class CanvasWrapper implements Canvas {
    private Map<Integer, Map<String, Object>> levelViewProperties;
    private android.graphics.Canvas canvas;
    private Paint paint;
    private PixelConverter pixelConverter;

    public void setPixelConverter(PixelConverter pixelConverter) {
        this.pixelConverter = pixelConverter;
    }

    public PixelConverter getPixelConverter() {
        return pixelConverter;
    }

    public void setCanvas(android.graphics.Canvas canvas) {
        this.canvas = canvas;
    }
    public android.graphics.Canvas getCanvas(){ return canvas; }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setLevelViewProperties(Map<Integer, Map<String, Object>> levelViewProperties) {
        this.levelViewProperties = levelViewProperties;
    }

    @Override
    public void drawBall(Ball b) {
        paint.setColor((Integer)(levelViewProperties.get(b.getId()).get(BuilderModel.Constants.KEY_COLOR_PRIMARY)));
        canvas.drawCircle(pixelConverter.pxFromDp(b.getX()),
                pixelConverter.pxFromDp(b.getY()),
                pixelConverter.pxFromDp(b.getR()),
                paint);
    }

    @Override
    public void drawObstacle(Obstacle ob) {
        paint.setColor((Integer)(levelViewProperties.get(ob.getId()).get(BuilderModel.Constants.KEY_COLOR_PRIMARY)));
        canvas.drawRect(
                pixelConverter.pxFromDp(ob.getX() - ob.getWidth() / 2),
                pixelConverter.pxFromDp(ob.getY() - ob.getHeight() / 2),
                pixelConverter.pxFromDp(ob.getX() + ob.getWidth() / 2),
                pixelConverter.pxFromDp(ob.getY() + ob.getHeight() / 2),
                paint);
    }

    @Override
    public void drawGoal(Hole h) {
        paint.reset();
        paint.setColor((Integer)(levelViewProperties.get(h.getId()).get(BuilderModel.Constants.KEY_COLOR_SECONDARY)));
        float x = pixelConverter.pxFromDp(h.getX());
        float y = pixelConverter.pxFromDp(h.getY());
        canvas.drawCircle(x, y, pixelConverter.pxFromDp(h.getR())*1.2f, paint);
        paint.setColor((Integer)(levelViewProperties.get(h.getId()).get(BuilderModel.Constants.KEY_COLOR_PRIMARY)));;
        canvas.drawCircle(x, y, pixelConverter.pxFromDp(h.getR()), paint);
    }

    @Override
    public void drawTrap(Hole h) {
        paint.reset();
        paint.setColor((Integer)(levelViewProperties.get(h.getId()).get(BuilderModel.Constants.KEY_COLOR_SECONDARY)));
        float x = pixelConverter.pxFromDp(h.getX());
        float y = pixelConverter.pxFromDp(h.getY());
        canvas.drawCircle(x, y, pixelConverter.pxFromDp(h.getR())*1.2f, paint);
        paint.setColor((Integer)(levelViewProperties.get(h.getId()).get(BuilderModel.Constants.KEY_COLOR_PRIMARY)));
        canvas.drawCircle(x, y, pixelConverter.pxFromDp(h.getR()), paint);
    }

    @Override
    public void drawBackground(Level l){
        paint.reset();
        paint.setColor((Integer)(levelViewProperties.get(BuilderModel.Constants.KEY_BACKGROUND).get(BuilderModel.Constants.KEY_COLOR_PRIMARY)));
        canvas.drawPaint(paint);
    }

    public void drawBallParams(Level.BallParams b){
        paint.reset();
        paint.setColor((Integer)(levelViewProperties.get(b.getId()).get(BuilderModel.Constants.KEY_COLOR_PRIMARY)));
        canvas.drawCircle(pixelConverter.pxFromDp(b.getStartX()),
                pixelConverter.pxFromDp(b.getStartY()),
                pixelConverter.pxFromDp(b.getR()),
                paint);

    }

}
