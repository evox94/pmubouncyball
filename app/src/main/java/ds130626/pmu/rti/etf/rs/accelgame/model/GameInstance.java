package ds130626.pmu.rti.etf.rs.accelgame.model;

import android.util.Log;

import java.io.Serializable;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ds130626.pmu.rti.etf.rs.accelgame.controller.GameEventListener;
import ds130626.pmu.rti.etf.rs.accelgame.controller.InstanceController;
import ds130626.pmu.rti.etf.rs.accelgame.model.util.GeometryHelpers;
import ds130626.pmu.rti.etf.rs.accelgame.model.util.PropertyBundle;
import ds130626.pmu.rti.etf.rs.accelgame.view.Canvas;
import ds130626.pmu.rti.etf.rs.accelgame.view.View;

/**
 * Created by smiljan on 1/31/17.
 */

public class GameInstance implements Serializable {
    private transient View view;
    private transient GameEventListener eventListener;
    private int refreshInterval;
    private long lastRefreshTime;
    private long lastUpdateTime;
    private long startTime;

    private Ball ball;
    private Level level;

    private float axOffset;
    private float ayOffset;
    private float noiseCutoffLevel;

    private float speedScale;

    private FrictionController friction;
    private BallBounceFilter bounceFilterX;
    private BallBounceFilter bounceFilterY;
    private float bounceFactor;

    private boolean gameOver;
    private boolean paused;

    private Map<String, ?> properties;

    public GameInstance(Level level, View view, GameEventListener eventListener, Map<String, ?> properties) {
        this.level = level;
        this.view = view;
        this.eventListener = eventListener;
        this.properties = properties;
        lastRefreshTime = 0;
        lastUpdateTime = 0;
        setProperties(properties);
        gameOver = false;
    }

    public GameInstance(Level level, View view, GameEventListener eventListener) {
        this(level, view, eventListener, new HashMap<String, Object>());
    }

    public InstanceController createController() {
        return new ControllerImpl();
    }

    private void setProperties(Map<String, ?> properties) {
        PropertyBundle p = new PropertyBundle(properties);

        int maxFps = p.getInt(Constants.KEY_MAX_FPS, Constants.DEFAULT_MAX_FPS);
        refreshInterval = 1000 / maxFps;

        ball = new Ball(level.getBallParams());

        axOffset = p.getFloat(Constants.KEY_AX_OFFSET, Constants.DEFAULT_AX_OFFSET);
        ayOffset = p.getFloat(Constants.KEY_AY_OFFSET, Constants.DEFAULT_AY_OFFSET);
        noiseCutoffLevel = p.getFloat(Constants.KEY_NOISE_CUTOFF_LEVEL, Constants.DEFAULT_NOISE_CUTOFF_LEVEL);

        speedScale = p.getFloat(Constants.KEY_SPEED_SCALE, Constants.DEFAULT_SPEED_SCALE);

        friction = new FrictionController(p.getFloat(Constants.KEY_FRICTION_COEFFICIENT, Constants.DEFAULT_FRICTION_COEFFICIENT));

        float filterStart = p.getFloat(Constants.KEY_BOUNCE_FILTER_START, Constants.DEFAULT_BOUNCE_FILTER_START);
        float filterAccuracy = p.getFloat(Constants.KEY_BOUNCE_FILTER_ACCURACY, Constants.DEFAULT_BOUNCE_FILTER_ACCURACY);
        int filterInterval = p.getInt(Constants.KEY_BOUNCE_FILTER_INTERVAL, Constants.DEFAULT_BOUNCE_FILTER_INTERVAL);
        float filterSpeedCutoff = p.getFloat(Constants.KEY_BOUNCE_FILTER_SPEED_CUTOFF, Constants.DEFAULT_BOUNCE_FILTER_SPEED_CUTOFF);
        bounceFilterX = new BallBounceFilter(filterStart, filterAccuracy, filterInterval, filterSpeedCutoff);
        bounceFilterY = new BallBounceFilter(filterStart, filterAccuracy, filterInterval, filterSpeedCutoff);

        bounceFactor = p.getFloat(Constants.KEY_BOUNCE_FACTOR, Constants.DEFAULT_BOUNCE_FACTOR);
    }

    private void update(float ax, float ay, float az, long currentTimeMillis) {
        if(gameOver){
            return;
        }

        if (lastUpdateTime == 0) {
            lastUpdateTime = currentTimeMillis;
            startTime = currentTimeMillis;
            return;
        }

        ax += axOffset;
        ay += ayOffset;

        if (Math.abs(ax) < noiseCutoffLevel) {
            ax = 0;
        }
        if (Math.abs(ay) < noiseCutoffLevel) {
            ay = 0;
        }

//        Log.i("AccelerationAfterOffset", ax + " " + ay);

        long t = currentTimeMillis - lastUpdateTime;
//        Log.i("Time", t+"");
//        Log.i("BallVelocityBefore", ball.vx + " " +ball.vy);
//        Log.i("BallPostiionBefore",ball.x+" "+ball.y);
//
//        Log.i("BallPostiionBefore", ball.x+" "+ball.y);

        friction.calculate(ball, ax, ay, az);
        ball.oldx = ball.x;
        ball.oldy = ball.y;
        ball.x = ball.x + ball.vx * t * speedScale;
        if (ball.adjustIfOutOfBoundsX(level.getWidth(), level.getObstacles())) {
            ball.vx = -ball.vx * bounceFactor;
            ball.vx = bounceFilterX.adjust(ball.vx, currentTimeMillis);
            if(ball.vx != 0){
                eventListener.onObstacleHit();
            }
        } else {
            float sign = Math.signum(ball.vx);
            ax += friction.getFrictionX();
            ball.vx += ax * t / 1000f;
            if (sign != Math.signum(ball.vx) && sign != 0) {
                ball.vx = 0;
            }
        }
        ball.y = ball.y + ball.vy * t * speedScale;
        if (ball.adjustIfOutOfBoundsY(level.getHeight(), level.getObstacles())) {
            ball.vy = -ball.vy * bounceFactor;
            ball.vy = bounceFilterY.adjust(ball.vy, currentTimeMillis);
            if(ball.vy != 0){
                eventListener.onObstacleHit();
            }
        } else {
            float sign = Math.signum(ball.vy);
            ay += friction.getFrictionY();
            ball.vy += ay * t / 1000f;
            if (sign != Math.signum(ball.vy) && sign != 0) {
                ball.vy = 0;
            }
        }

        Log.i("BallPostiionAfter",ball.x+" "+ball.y);
        //Log.i("BallAccel", ax+" "+ ay);
        lastUpdateTime = currentTimeMillis;
        updateView(currentTimeMillis);

        if(level.getGoal()!= null && GeometryHelpers.pointIsInCircle(ball.x, ball.y, level.getGoal().getX(), level.getGoal().getY(), level.getGoal().getR())){
            eventListener.onGameOver(true, currentTimeMillis - startTime);
            gameOver = true;
        }else{
            for(Hole trap: level.getTraps()){
                if(GeometryHelpers.pointIsInCircle(ball.x, ball.y, trap.getX(), trap.getY(), trap.getR())){
                    eventListener.onGameOver(false, currentTimeMillis - startTime);
                    gameOver = true;
                    break;
                }
            }
        }
    }

    private void updateView(long currentTimeMillis) {
        if (currentTimeMillis - lastRefreshTime > refreshInterval) {
            lastRefreshTime = currentTimeMillis;
            view.refresh();
        }
    }

    public void redraw(Canvas canvas) {
        canvas.drawBackground(level);
        if(level.getGoal()!=null){
            canvas.drawGoal(level.getGoal());
        }
        for(Hole h: level.getTraps()){
            canvas.drawTrap(h);
        }
        for(Obstacle o: level.getObstacles()){
            canvas.drawObstacle(o);
        }
        if(ball!=null){
            canvas.drawBall(ball);
        }
    }

    public static class Constants {
        public static final int DEFAULT_MAX_FPS = 60;
        public static final float DEFAULT_BALL_RADIUS = 10;
        public static final float DEFAULT_BALL_MASS = 0.01f;
        public static final float DEFAULT_FRICTION_COEFFICIENT = 0.1f;
        public static final float DEFAULT_AX_OFFSET = 0;
        public static final float DEFAULT_AY_OFFSET = 0;
        public static final float DEFAULT_NOISE_CUTOFF_LEVEL = 0.1f;
        public static final float DEFAULT_SPEED_SCALE = 0.5f;
        public static final float DEFAULT_BOUNCE_FILTER_START = 0.5f;
        public static final float DEFAULT_BOUNCE_FILTER_ACCURACY = 0.01f;
        public static final int DEFAULT_BOUNCE_FILTER_INTERVAL = 150;
        public static final float DEFAULT_BOUNCE_FILTER_SPEED_CUTOFF = 0.05f;
        public static final float DEFAULT_BOUNCE_FACTOR = 0.7f;

        public static final String KEY_MAX_FPS = "ds130626.pmu.rti.etf.rs.accelgame.maxFPS";
        public static final String KEY_BALL_RADIUS = "ds130626.pmu.rti.etf.rs.accelgame.ballRadius";
        public static final String KEY_BALL_MASS = "ds130626.pmu.rti.etf.rs.accelgame.ballMass";
        public static final String KEY_FRICTION_COEFFICIENT = "ds130626.pmu.rti.etf.rs.accelgame.frictionCoefficient";
        public static final String KEY_AX_OFFSET = "ds130626.pmu.rti.etf.rs.accelgame.axOffset";
        public static final String KEY_AY_OFFSET = "ds130626.pmu.rti.etf.rs.accelgame.ayOffset";
        public static final String KEY_NOISE_CUTOFF_LEVEL = "ds130626.pmu.rti.etf.rs.accelgame.noiseCutoffLevel";
        public static final String KEY_SPEED_SCALE = "ds130626.pmu.rti.etf.rs.accelgame.speedScale";
        public static final String KEY_BOUNCE_FILTER_START = "ds130626.pmu.rti.etf.rs.accelgame.bounceFilterStart";
        public static final String KEY_BOUNCE_FILTER_ACCURACY = "ds130626.pmu.rti.etf.rs.accelgame.bounceFilterAccuracy";
        public static final String KEY_BOUNCE_FILTER_INTERVAL = "ds130626.pmu.rti.etf.rs.accelgame.bounceFilterInterval";
        public static final String KEY_BOUNCE_FILTER_SPEED_CUTOFF = "ds130626.pmu.rti.etf.rs.accelgame.bounceFilterSpeedCutoff";
        public static final String KEY_BOUNCE_FACTOR = "ds130626.pmu.rti.etf.rs.accelgame.bounceFactor";
    }

    private class ControllerImpl implements InstanceController {
        boolean start = false;

        @Override
        public void update(float ax, float ay, float az, long currentTimeMillis) {
            if (start) {
                if(paused && lastUpdateTime!=0){
                    long dt = currentTimeMillis - GameInstance.this.lastUpdateTime;
                    lastUpdateTime+=dt;
                    startTime+=dt;
                    paused = false;
                }
                GameInstance.this.update(ax, ay, az, currentTimeMillis);
            }
        }

        @Override
        public GameInstance getGameInstance() {
            return GameInstance.this;
        }

        @Override
        public void acceptInput() {
            start = true;
        }

        @Override
        public void pause(){
            start = false;
            paused = true;
        }

        @Override
        public void restart() {
            pause();
            lastRefreshTime = 0;
            lastUpdateTime = 0;
            setProperties(properties);
            gameOver = false;
            acceptInput();
        }
    }
}
