package ds130626.pmu.rti.etf.rs.accelgame.android.build;

import ds130626.pmu.rti.etf.rs.accelgame.model.GameInstance;

/**
 * Created by smiljan on 2/11/17.
 */

public class SelectionModel {
    private float ballR = GameInstance.Constants.DEFAULT_BALL_RADIUS;
    private int ballColor = BuilderModel.Constants.DEFAULT_BALL_COLOR;
    private int ballColorProgress = BuilderModel.Constants.DEFAULT_BALL_COLOR_PROGRESS;
    private int goalColorPrimary = BuilderModel.Constants.DEFAULT_HOLE_COLOR;
    private int goalColorPrimaryProgress = BuilderModel.Constants.DEFAULT_HOLE_COLOR_PROGRESS;
    private int goalColorSecondary = BuilderModel.Constants.DEFAULT_GOAL_SECONDARY;
    private int goalColorSecondaryProgress = BuilderModel.Constants.DEFAULT_GOAL_SECONDARY_PROGRESS;
    private int goalRadius = BuilderModel.Constants.DEFAULT_GOAL_RADIUS;
    private int trapColorPrimary = BuilderModel.Constants.DEFAULT_HOLE_COLOR;
    private int trapColorPrimaryProgress = BuilderModel.Constants.DEFAULT_HOLE_COLOR_PROGRESS;
    private int trapColorSecondary = BuilderModel.Constants.DEFAULT_TRAP_SECONDARY;
    private int trapColorSecondaryProgress = BuilderModel.Constants.DEFAULT_TRAP_SECONDARY_PROGRESS;
    private int trapRadius = BuilderModel.Constants.DEFAULT_GOAL_RADIUS;
    private int obstacleColor = BuilderModel.Constants.DEFAULT_OBSTACLE_COLOR;
    private int obstacleColorProgress = BuilderModel.Constants.DEFAULT_OBSTACLE_COLOR_PROGRESS;
    private int backgroundColor = BuilderModel.Constants.DEFAULT_BACKGROUND_COLOR;
    private int backgroundColorProgress = BuilderModel.Constants.DEFAULT_BACKGROUND_COLOR_PROGRESS;
    private int selectedObject = NO_OBJECT;
    public static final int NO_OBJECT = 1;
    public static final int BALL = 2;
    public static final int OBSTACLE = 3;
    public static final int GOAL = 4;
    public static final int TRAP = 5;

    public float getBallR() {
        return ballR;
    }

    public void setBallR(float ballR) {
        this.ballR = ballR;
    }

    public int getBallColor() {
        return ballColor;
    }

    public void setBallColor(int ballColor) {
        this.ballColor = ballColor;
    }

    public void setSelectedObject(int selection) {
        selectedObject = selection;
    }

    public int getBallColorProgress() {
        return ballColorProgress;
    }

    public void setBallColorProgress(int ballColorProgress) {
        this.ballColorProgress = ballColorProgress;
    }

    public int getGoalColorPrimary() {
        return goalColorPrimary;
    }

    public void setGoalColorPrimary(int goalColorPrimary) {
        this.goalColorPrimary = goalColorPrimary;
    }

    public int getGoalColorPrimaryProgress() {
        return goalColorPrimaryProgress;
    }

    public void setGoalColorPrimaryProgress(int goalColorPrimaryProgress) {
        this.goalColorPrimaryProgress = goalColorPrimaryProgress;
    }

    public int getGoalColorSecondary() {
        return goalColorSecondary;
    }

    public void setGoalColorSecondary(int goalColorSecondary) {
        this.goalColorSecondary = goalColorSecondary;
    }

    public int getGoalColorSecondaryProgress() {
        return goalColorSecondaryProgress;
    }

    public void setGoalColorSecondaryProgress(int goalColorSecondaryProgress) {
        this.goalColorSecondaryProgress = goalColorSecondaryProgress;
    }

    public int getGoalRadius() {
        return goalRadius;
    }

    public void setGoalRadius(int goalRadius) {
        this.goalRadius = goalRadius;
    }

    public int getSelectedObject() {
        return selectedObject;
    }

    public int getObstacleColor() {
        return obstacleColor;
    }

    public void setObstacleColor(int obstacleColor) {
        this.obstacleColor = obstacleColor;
    }

    public int getObstacleColorProgress() {
        return obstacleColorProgress;
    }

    public void setObstacleColorProgress(int obstacleColorProgress) {
        this.obstacleColorProgress = obstacleColorProgress;
    }

    public int getTrapColorPrimary() {
        return trapColorPrimary;
    }

    public void setTrapColorPrimary(int trapColorPrimary) {
        this.trapColorPrimary = trapColorPrimary;
    }

    public int getTrapColorPrimaryProgress() {
        return trapColorPrimaryProgress;
    }

    public void setTrapColorPrimaryProgress(int trapColorPrimaryProgress) {
        this.trapColorPrimaryProgress = trapColorPrimaryProgress;
    }

    public int getTrapColorSecondary() {
        return trapColorSecondary;
    }

    public void setTrapColorSecondary(int trapColorSecondary) {
        this.trapColorSecondary = trapColorSecondary;
    }

    public int getTrapColorSecondaryProgress() {
        return trapColorSecondaryProgress;
    }

    public void setTrapColorSecondaryProgress(int trapColorSecondaryProgress) {
        this.trapColorSecondaryProgress = trapColorSecondaryProgress;
    }

    public int getTrapRadius() {
        return trapRadius;
    }

    public void setTrapRadius(int trapRadius) {
        this.trapRadius = trapRadius;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getBackgroundColorProgress() {
        return backgroundColorProgress;
    }

    public void setBackgroundColorProgress(int backgroundColorProgress) {
        this.backgroundColorProgress = backgroundColorProgress;
    }
}
