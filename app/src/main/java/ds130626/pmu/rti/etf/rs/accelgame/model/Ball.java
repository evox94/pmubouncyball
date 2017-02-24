package ds130626.pmu.rti.etf.rs.accelgame.model;

import android.opengl.GLES31Ext;
import android.util.Log;

import java.io.Serializable;
import java.util.List;

import ds130626.pmu.rti.etf.rs.accelgame.model.util.GeometryHelpers;

/**
 * Created by smiljan on 1/31/17.
 */

public class Ball implements Serializable {
    private int id;
    float x;
    float oldx;
    float y;
    float oldy;
    float r;
    float mass;
    float vx;
    float vy;

    public Ball(float x, float y, float r, float mass) {
        this.x = x;
        oldx = x;
        this.y = y;
        oldy = y;
        this.r = r;
        this.mass = mass;
        vx = 0;
        vy = 0;
    }

    public Ball(Level.BallParams params){
        this(params.getStartX(), params.getStartY(), params.getR(), params.getMass());
        id = params.getId();
    }

    public float getX() {
        return x;
    }

    void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    void setY(float y) {
        this.y = y;
    }

    public float getR() {
        return r;
    }

    void setR(float r) {
        this.r = r;
    }

    public float getVx() {
        return vx;
    }

    void setVx(float vx) {
        this.vx = vx;
    }

    public float getVy() {
        return vy;
    }

    void setVy(float vy) {
        this.vy = vy;
    }


    boolean adjustIfOutOfBoundsX(float width, List<Obstacle> obstacles) {
        boolean adjusted = false;
        if (x + r > width) {
            x = width - r ;
            adjusted = true;
        } else if (x - r < 0) {
            x = r;
            adjusted = true;
        }

        for(Obstacle o: obstacles){
            int intersects = GeometryHelpers.circleRectangleIntersection(x,y,r,o.getX(),o.getY(),o.getWidth(),o.getHeight());;
            if(intersects!=GeometryHelpers.NO_INTERSECT || ghostIntersectX(o)){
                float f = r;
                if(intersects == GeometryHelpers.CORNER_INTERSECT){
                    float distanceY = Math.abs(o.getY() - y);
                    f =(float)Math.sqrt(r*r - (distanceY - o.height/2)*(distanceY - o.height/2));
                }
                float right = Math.abs(oldx-(o.getX()+o.width/2));
                float left = Math.abs(oldx-(o.getX()-o.width/2));
                //x = o.getX()+(o.width/2+f+0.0001f)*-Math.signum(vx);
                x = o.getX()+(o.width/2+f+0.0001f)*(left<right?-1:1);
                adjusted = true;
            }
        }
        return adjusted;
    }

    private boolean ghostIntersectX(Obstacle o) {
        if(oldy+r > o.getY()-o.getHeight()/2 && oldy-r < o.getY() + o.getHeight()/2){
            if(Math.signum(oldx-o.getX()) != Math.signum(x-o.getX())){
                return true;
            }
        }
        return false;
    }

    private boolean ghostIntersectY(Obstacle o) {
        if(oldx+r > o.getX()-o.getWidth()/2 && oldx-r < o.getX() + o.getWidth()/2){
            if(Math.signum(oldy-o.getY()) != Math.signum(y-o.getY())){
                Log.i("GhostIntersect", "Y");
                return true;
            }
        }
        return false;
    }

    boolean adjustIfOutOfBoundsY(float height, List<Obstacle> obstacles) {
        boolean adjusted = false;
        if (y + r > height) {
            y = height - r;
            adjusted = true;
        } else if (y - r < 0) {
            y = r;
            adjusted = true;
        }

        for(Obstacle o: obstacles){
            int intersects = GeometryHelpers.circleRectangleIntersection(x,y,r,o.getX(),o.getY(),o.getWidth(),o.getHeight());
            if(intersects != GeometryHelpers.NO_INTERSECT || ghostIntersectY(o)){
                float f = r;
                if(intersects == GeometryHelpers.CORNER_INTERSECT){
                    float distanceX = Math.abs(o.getX() - x);
                    f =(float)Math.sqrt(r*r - (distanceX - o.width/2)*(distanceX - o.width/2));
                }
                float bottom = Math.abs(oldy-(o.getY()+o.height/2));
                float top = Math.abs(oldy-(o.getY()-o.height/2));
                //y = o.getY()+(o.height/2+f+0.0001f)*-Math.signum(vy);
                y = o.getY()+(o.height/2+f+0.0001f)*(top<bottom?-1:1);
                adjusted = true;
            }
        }
        return adjusted;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
