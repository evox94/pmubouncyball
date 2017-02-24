package ds130626.pmu.rti.etf.rs.accelgame.controller;

/**
 * Created by smiljan on 1/31/17.
 */

public interface GameEventListener {
    void onGameOver(boolean outcome, long time);
    void onObstacleHit();
}
