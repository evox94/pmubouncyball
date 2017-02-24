package ds130626.pmu.rti.etf.rs.accelgame.android;

import android.graphics.Bitmap;

import ds130626.pmu.rti.etf.rs.accelgame.android.build.LevelWrapper;

/**
 * Created by smiljan on 2/12/17.
 */

public interface LevelLoader {
    void save(LevelWrapper wrapper);
    LevelWrapper load(String name);
    Bitmap loadLevelPic(String name);
    void delete(String levelName);
}
