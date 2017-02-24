package ds130626.pmu.rti.etf.rs.accelgame.view;

import ds130626.pmu.rti.etf.rs.accelgame.model.Ball;
import ds130626.pmu.rti.etf.rs.accelgame.model.Hole;
import ds130626.pmu.rti.etf.rs.accelgame.model.Level;
import ds130626.pmu.rti.etf.rs.accelgame.model.Obstacle;

/**
 * Created by smiljan on 1/31/17.
 */

public interface Canvas {
    void drawBall(Ball b);
    void drawObstacle(Obstacle ob);
    void drawGoal(Hole h);
    void drawTrap(Hole h);
    void drawBackground(Level l);
}
