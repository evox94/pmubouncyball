package ds130626.pmu.rti.etf.rs.accelgame.model;

import java.io.Serializable;

/**
 * Created by smiljan on 2/2/17.
 */

public class FrictionController implements Serializable{
    private float frictionCoefficient;
    private float frictionalAx = 0;
    private float frictionalAy = 0;
    public FrictionController(float frictionCoeffecient) {
        this.frictionCoefficient = frictionCoeffecient;
    }

    public float getFrictionCoefficient() {
        return frictionCoefficient;
    }

    public void setFrictionCoefficient(float frictionCoefficient) {
        this.frictionCoefficient = frictionCoefficient;
    }

    public void calculate(Ball ball, float ax, float ay, float az){
        az = Math.abs(az);
        float frictionForce = az * ball.mass * frictionCoefficient;
        if(ball.vx==0 && ball.vy ==0){
            frictionalAx = 0;
            frictionalAy = 0;
        } else if(ball.vx == 0){
            frictionalAx = 0;
            frictionalAy = (frictionForce/ball.mass);
        } else if(ball.vy == 0){
            frictionalAx = (frictionForce/ball.mass);
            frictionalAy = 0;
        }else{
            double angle = Math.atan(ball.vy/ball.vx);
            frictionalAx = (float)(frictionForce*Math.cos(angle)/ball.mass);
            frictionalAy = (float)(frictionForce*Math.sin(angle)/ball.mass);
        }

        frictionalAx = Math.abs(frictionalAx)*-Math.signum(ball.vx);
        frictionalAy = Math.abs(frictionalAy)*-Math.signum(ball.vy);
    }
    public float getFrictionX() {
        return frictionalAx;
    }

    public float getFrictionY() {
        return frictionalAy;
    }

}
