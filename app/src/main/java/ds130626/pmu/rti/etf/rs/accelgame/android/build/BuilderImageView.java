package ds130626.pmu.rti.etf.rs.accelgame.android.build;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.Map;

import ds130626.pmu.rti.etf.rs.accelgame.android.CanvasWrapper;
import ds130626.pmu.rti.etf.rs.accelgame.android.PixelConverter;
import ds130626.pmu.rti.etf.rs.accelgame.view.View;

/**
 * Created by smiljan on 2/11/17.
 */

public class BuilderImageView extends ImageView implements View {
    Paint paint;
    BuilderModel model;
    CanvasWrapper canvasWrapper;
    PixelConverter pixelConverter;

    public BuilderImageView(Context context) {
        super(context);
        init();
    }

    public BuilderImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BuilderImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        paint = new Paint();
        canvasWrapper = new CanvasWrapper();
        canvasWrapper.setPaint(paint);
    }


    public void setModel(BuilderModel model) {
        this.model = model;
        canvasWrapper.setLevelViewProperties(model.getLevelViewProperties());

    }

    public void setPixelConverter(PixelConverter pixelConverter) {
        this.pixelConverter = pixelConverter;
        canvasWrapper.setPixelConverter(pixelConverter);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvasWrapper.setCanvas(canvas);
        if(model!=null){
            model.redraw(canvasWrapper);
        }
    }

    @Override
    public void refresh() {
        invalidate();
    }
}
