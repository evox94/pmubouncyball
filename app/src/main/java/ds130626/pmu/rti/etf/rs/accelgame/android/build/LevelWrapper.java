package ds130626.pmu.rti.etf.rs.accelgame.android.build;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Map;

import ds130626.pmu.rti.etf.rs.accelgame.model.Level;

/**
 * Created by smiljan on 2/12/17.
 */

public class LevelWrapper implements Serializable {
    private Level level;
    private Map<Integer, Map<String, Object>> levelViewProperties;
    transient private Bitmap bitmap;

    public LevelWrapper(Level level, Map<Integer, Map<String, Object>> levelViewProperties, Bitmap levelPic, float density) {
        float aspectRatio = levelPic.getWidth() /
                (float) levelPic.getHeight();
        int width = Math.round(300/density);
        int height = Math.round(width / aspectRatio);

        bitmap = Bitmap.createScaledBitmap(
                levelPic, width, height, false);
        this.level = level;
        this.levelViewProperties = levelViewProperties;
    }

    public Level getLevel() {
        return level;
    }

    public Map<Integer, Map<String, Object>> getLevelViewProperties() {
        return levelViewProperties;
    }

    @Override
    public String toString() {
        return level.getName();
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
