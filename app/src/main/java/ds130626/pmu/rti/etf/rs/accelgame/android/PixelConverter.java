package ds130626.pmu.rti.etf.rs.accelgame.android;

/**
 * Created by smiljan on 2/1/17.
 */

public class PixelConverter {

    private float density;

    public PixelConverter(float density) {
        this.density = density;
    }

    public float dpFromPx(final float px) {
        return px / density;
    }

    public float pxFromDp(final float dp) {
        return dp * density;
    }
}
