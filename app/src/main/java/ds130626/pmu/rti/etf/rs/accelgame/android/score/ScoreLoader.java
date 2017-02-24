package ds130626.pmu.rti.etf.rs.accelgame.android.score;

import android.database.Cursor;

/**
 * Created by smiljan on 2/13/17.
 */

public interface ScoreLoader {
    void put(long time, String name, String level);
    Object getScores(String level);
    void deleteForLevel(String level);
    void deleteAll();
}
