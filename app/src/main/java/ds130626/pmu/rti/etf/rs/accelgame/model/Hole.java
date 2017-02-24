package ds130626.pmu.rti.etf.rs.accelgame.model;

import java.io.Serializable;

/**
 * Created by smiljan on 2/9/17.
 */

public class Hole implements Serializable {
    private int id;
    float x;
    float y;
    float r;

    public Hole(float x, float y, float r) {
        this.x = x;
        this.y = y;
        this.r = r;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hole hole = (Hole) o;

        if (Float.compare(hole.x, x) != 0) return false;
        if (Float.compare(hole.y, y) != 0) return false;
        return Float.compare(hole.r, r) == 0;

    }

    @Override
    public int hashCode() {
        int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
        result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
        result = 31 * result + (r != +0.0f ? Float.floatToIntBits(r) : 0);
        return result;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getR() {
        return r;
    }

    public void setR(float r) {
        this.r = r;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
