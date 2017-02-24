package ds130626.pmu.rti.etf.rs.accelgame.android.build;

import android.graphics.Canvas;
import android.graphics.Paint;

import ds130626.pmu.rti.etf.rs.accelgame.android.PixelConverter;

/**
 * Created by smiljan on 2/11/17.
 */

public abstract class DrawablePreview {
    protected SelectionModel model;

    public DrawablePreview(SelectionModel model) {
        this.model = model;
    }

    public abstract void draw(Canvas canvas, Paint paint, PixelConverter converter);
}
