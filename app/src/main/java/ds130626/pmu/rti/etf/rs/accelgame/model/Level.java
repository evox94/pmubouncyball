package ds130626.pmu.rti.etf.rs.accelgame.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import ds130626.pmu.rti.etf.rs.accelgame.android.build.Undoable;
import ds130626.pmu.rti.etf.rs.accelgame.model.util.GeometryHelpers;


/**
 * Created by smiljan on 1/31/17.
 */

public class Level implements Serializable {
    private float width;
    private float height;
    private BallParams ballParams = null;
    private List<Obstacle> obstacles;
    private Hole goal;
    private List<Hole> traps;
    private String name;

    private Level(float width, float height) {
        this.width = width;
        this.height = height;
        obstacles = new ArrayList<>();
        traps = new ArrayList<>();
    }

    public BallParams getBallParams() {
        return ballParams;
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public Hole getGoal() {
        return goal;
    }

    public List<Hole> getTraps() {
        return traps;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public String getName() {
        return name;
    }

    public static class BallParams implements Serializable{
        private int id;
        private float startX;
        private float startY;
        private float r;
        private float mass;

        public BallParams(float startX, float startY, float r, float mass) {
            this.startX = startX;
            this.startY = startY;
            this.r = r;
            this.mass = mass;
        }

        public float getStartX() {
            return startX;
        }

        public void setStartX(float startX) {
            this.startX = startX;
        }

        public float getStartY() {
            return startY;
        }

        public void setStartY(float startY) {
            this.startY = startY;
        }

        public float getR() {
            return r;
        }

        public void setR(float r) {
            this.r = r;
        }

        public float getMass() {
            return mass;
        }

        public void setMass(float mass) {
            this.mass = mass;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    public static Level.Builder createBuilder(){
        Level level = new Level(0, 0);
        return level.new Builder();

    }

    public class Builder{
        private int nextId = 3;
        private final static int BALLID = 1;
        private final static int GOALID = 2;

        public int getNextId() {
            return nextId;
        }

        public int setBall(BallParams params){
            Level.this.ballParams = params;
            if(params!=null){
                params.setId(BALLID);
                return BALLID;
            }
            return -1;
        }

        public int setGoal(Hole goal){
            Level.this.goal = goal;
            if(goal!=null){
                goal.setId(GOALID);
                return GOALID;
            }
            return -1;
        }

        public int addObstacle(Obstacle obstacle){
            obstacle.setId(nextId);
            obstacles.add(obstacle);
            return nextId++;
        }

        public void removeObstacle(int id){
            int i=-1;
            for(Obstacle o :obstacles){
                i++;
                if(o.getId()==id){
                    break;
                }
            }
            if(i>-1 && i <obstacles.size()){
                obstacles.remove(i);
            }
        }

        public int addTrap(Hole trap){
            trap.setId(nextId);
            traps.add(trap);
            return nextId++;
        }

        public void removeTrap(int id){
            int i=-1;
            for(Hole hole :traps){
                i++;
                if(hole.getId()==id){
                    break;
                }
            }
            if(i>-1 && i <traps.size()){
                traps.remove(i);
            }
        }

        public void setName(final String name){
            Level.this.name = name;
        }

        public void setWidth(float width){
            Level.this.width = width;
        }

        public void setHeight(float height){
            Level.this.height = height;
        }

        private boolean circleOutOfBounds(float x, float y, float r){
            return x + r > width || x - r < 0 || y + r > height || y - r < 0;
        }

        private boolean ballHoleValidPosition(Level.BallParams ball, Hole hole){
            return ball==null || hole==null || !GeometryHelpers.pointIsInCircle(ball.getStartX(), ball.getStartY(), hole.getX(), hole.getY(), hole.getR());
        }

        private boolean circleIntersectsObstacles(float x, float y, float r){
            boolean intersects = false;
            for (Obstacle o : obstacles) {
                if (GeometryHelpers.circleRectangleIntersection(x, y, r, o.getX(), o.getY(), o.getWidth(), o.getHeight()) != GeometryHelpers.NO_INTERSECT) {
                    intersects = true;
                    break;
                }
            }
            return intersects;
        }

        public boolean canPlaceBall(BallParams ball){
            //Goal
            if (!ballHoleValidPosition(ball, goal)) {return false;}
            //Obstacles
            if(circleIntersectsObstacles(ball.getStartX(), ball.getStartY(), ball.getR())){return false;}
            //Traps
            for (Hole h : traps) {
                if (GeometryHelpers.pointIsInCircle(ball.getStartX(), ball.getStartY(), h.getX(), h.getY(), h.getR())) {
                    return false;
                }
            }
            //Bounds
            if (circleOutOfBounds(ball.getStartX(), ball.getStartY(), ball.getR())) {
                return false;
            }

            return true;
        }

        public boolean canPlaceGoal(Hole goal){
            //Ball
            if (!ballHoleValidPosition(ballParams, goal)) {return false;}

            //Obstacles
            if(circleIntersectsObstacles(goal.getX(), goal.getY(), goal.getR())){return false;}

            for (Hole h : traps) {
                if (GeometryHelpers.circleCircleIntersection(goal.getX(), goal.getY(), goal.getR(), h.getX(),h.getY(),h.getR())) {
                    return false;
                }
            }
            if (circleOutOfBounds(goal.getX(), goal.getY(), goal.getR())) {
                return false;
            }
            return true;
        }

        public boolean canPlaceObstacle(Obstacle obstacle){
            //Ball
            if(ballParams!=null && GeometryHelpers.circleRectangleIntersection(ballParams.getStartX(), ballParams.getStartY(), ballParams.getR(),
                    obstacle.getX(), obstacle.getY(), obstacle.getWidth(), obstacle.getHeight()) != GeometryHelpers.NO_INTERSECT){
                return false;
            }

            //Goal
            if(goal!=null && GeometryHelpers.circleRectangleIntersection(goal.getX(), goal.getY(), goal.getR(),
                    obstacle.getX(), obstacle.getY(), obstacle.getWidth(), obstacle.getHeight()) != GeometryHelpers.NO_INTERSECT){
                return false;
            }

            //Trap
            for(Hole h: traps){
                if(GeometryHelpers.circleRectangleIntersection(h.getX(), h.getY(), h.getR(),
                        obstacle.getX(), obstacle.getY(), obstacle.getWidth(), obstacle.getHeight())!= GeometryHelpers.NO_INTERSECT) {
                    return false;
                }
            }
            return true;
        }

        public boolean canPlaceTrap(Hole trap){
            //Ball
            if (!ballHoleValidPosition(ballParams, trap)) {return false;}

            //Goal
            if (goal!=null && GeometryHelpers.circleCircleIntersection(goal.getX(), goal.getY(), goal.getR(), trap.getX(), trap.getY(), trap.getR())) {
                return false;
            }

            //Obstacles
            if(circleIntersectsObstacles(trap.getX(), trap.getY(), trap.getR())){return false;}

            return true;
        }

        public boolean hasName(){
            return name!=null && !name.isEmpty();
        }

        public boolean hasGoal(){
            return goal!=null;
        }

        public boolean hasBall(){
            return ballParams!=null;
        }

        public boolean isValid(){
            return hasName()&&hasBall()&&hasGoal();
        }

        public String getName(){
            return name;
        }

        public Level create(){return Level.this;}

        public void resetObjects() {
            ballParams = null;
            goal = null;
            obstacles.clear();
            traps.clear();
        }
    }
}
