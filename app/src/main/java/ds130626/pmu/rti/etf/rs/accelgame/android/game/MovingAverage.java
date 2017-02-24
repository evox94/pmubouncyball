package ds130626.pmu.rti.etf.rs.accelgame.android.game;

/**
 * Created by smiljan on 1/30/17.
 */

public class MovingAverage implements Filter {

    protected float values[][];
    protected float avg[];
    protected int dimension;
    protected int cap;
    protected int currentCap;
    protected int nextFree;

    public MovingAverage(int dimension, int cap) {
        this.dimension = dimension;
        this.cap = cap;
        values = new float[dimension][cap];
        avg = new float[dimension];
        currentCap = 0;
        nextFree = 0;
    }

    @Override
    public void filter(float[] newValues) {
        if (currentCap < cap) {
            for (int i = 0; i < dimension; i++) {
                values[i][currentCap] = newValues[i];
                avg[i] = (avg[i]*currentCap + newValues[i])/(currentCap+1);
            }
            currentCap++;
        } else {
            for (int i = 0; i < dimension; i++) {
                avg[i] = avg[i] + (newValues[i]-values[i][nextFree])/(cap);
                values[i][nextFree] = newValues[i];
            }
        }
        nextFree++;
        if (nextFree >= cap){
            nextFree = 0;
        }

        for (int i = 0; i < dimension; i++) {
            newValues[i] = avg[i];
        }
    }
}
