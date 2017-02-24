package ds130626.pmu.rti.etf.rs.accelgame.model.util;

import ds130626.pmu.rti.etf.rs.accelgame.model.Hole;
import ds130626.pmu.rti.etf.rs.accelgame.model.Obstacle;

/**
 * Created by smiljan on 2/10/17.
 */

public class GeometryHelpers {
    public static final int NO_INTERSECT = 0;
    public static final int SIMPLE_INTERSECT = 1;
    public static final int CORNER_INTERSECT = 2;
    public static int circleRectangleIntersection(float cx, float cy, float r, float rectX, float rectY, float width, float height){
        float distanceX = Math.abs(rectX - cx);
        float distanceY = Math.abs(rectY - cy);

        if (distanceX > (width/2 + r)) { return NO_INTERSECT; }
        if (distanceY > (height/2 + r)) { return NO_INTERSECT; }

        if (distanceX <= (width/2)) { return SIMPLE_INTERSECT; }
        if (distanceY <= (height/2)) { return SIMPLE_INTERSECT; }

        float cornerDistance_sq = (distanceX - width/2)*(distanceX - width/2) +
                (distanceY - height/2)*(distanceY - height/2);

        if (cornerDistance_sq <= (r*r)){
            return CORNER_INTERSECT;
        }
        return NO_INTERSECT;
    }

    public static boolean pointIsInCircle(float x, float y, float cx, float cy, float r){
        return Math.pow(x-cx,2)+Math.pow(y-cy,2) < r*r;
    }

    public static boolean circleCircleIntersection(float x1, float y1, float r1, float x2, float y2, float r2){
        return Math.pow(x1-x2,2)+Math.pow(y1-y2,2) < Math.pow(r1+r2,2);
    }
}
