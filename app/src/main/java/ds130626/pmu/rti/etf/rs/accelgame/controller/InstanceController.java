package ds130626.pmu.rti.etf.rs.accelgame.controller;

import ds130626.pmu.rti.etf.rs.accelgame.model.GameInstance;

/**
 * Created by smiljan on 1/31/17.
 */

public interface InstanceController{
    void update(float ax, float ay, float az, long currentTimeMillis);
    GameInstance getGameInstance();
    void acceptInput();
    void pause();
    void restart();
}
