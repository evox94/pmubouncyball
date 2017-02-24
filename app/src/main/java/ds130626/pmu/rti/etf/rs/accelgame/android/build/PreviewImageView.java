package ds130626.pmu.rti.etf.rs.accelgame.android.build;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import ds130626.pmu.rti.etf.rs.accelgame.android.PixelConverter;

/**
 * Created by smiljan on 2/11/17.
 */

public class PreviewImageView extends ImageView {
    DrawablePreview preview;
    Paint paint;
    PixelConverter pixelConverter;

    public PreviewImageView(Context context) {
        super(context);
        init();
    }

    public PreviewImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PreviewImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        paint = new Paint();
    }

    public DrawablePreview getPreview() {
        return preview;
    }

    public void setPreview(DrawablePreview preview) {
        this.preview = preview;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public PixelConverter getPixelConverter() {
        return pixelConverter;
    }

    public void setPixelConverter(PixelConverter pixelConverter) {
        this.pixelConverter = pixelConverter;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(preview!=null){
            preview.draw(canvas,paint,pixelConverter);
        }
    }
}
