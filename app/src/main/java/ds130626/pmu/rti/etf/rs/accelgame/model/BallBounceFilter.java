package ds130626.pmu.rti.etf.rs.accelgame.model;

import android.renderscript.Script;
import android.util.Log;

import java.io.Serializable;

/**
 * Created by smiljan on 2/2/17.
 */

public class BallBounceFilter implements Serializable{
    private float filterStart;
    private float accuracy;
    private int interval;
    private float speedCutoff;

    private long lastTime = 0;
    private float lastSpeed = 0;

    public BallBounceFilter(float filterStart, float accuracy, int interval, float speedCutoff) {
        this.filterStart = filterStart;
        this.accuracy = accuracy;
        this.interval = interval;
        this.speedCutoff = speedCutoff;
    }

    public float adjust(float v, long time) {
        long slice = time - lastTime;
        if (slice >= interval) {
            lastTime = time;
            return v;
        }
        lastTime = time;
        float absV = Math.abs(v);

        float retSpeed;
        if (absV > filterStart) {
            return v;
        } else {
            if (v < lastSpeed + accuracy && v > lastSpeed - accuracy) {
                retSpeed = 0;
            } else {
                retSpeed = v * absV / filterStart;
                if (Math.abs(retSpeed) < speedCutoff) {
                    return 0;
                }
            }
        }
        lastSpeed = v;
        return retSpeed;
    }
}
