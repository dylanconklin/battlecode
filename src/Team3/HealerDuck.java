package Team3;

import battlecode.common.*;

public class HealerDuck extends Duck {
    static Direction direction;
    public HealerDuck(RobotController rc) {
        super(rc);
        skill = SkillType.HEAL;
    }

    @Override public void play() throws GameActionException {
        super.play();
    }

    public void heal() throws GameActionException {}
    public void move() throws GameActionException {
        MapLocation[] locations = rc.getAllySpawnLocations();
        if (rc.hasFlag()) {
            // move toward ally spawn locations
            // TODO: don't move blindly toward locations[0]
            moveToward(locations[0]);
        } else {
            // move toward adversary spawn locations
            // TODO: don't move blindly away from locations[0]
            moveAwayFrom(locations[0]);
        }
    }

    @Override public void moveToward(MapLocation location) throws GameActionException {
        Direction direction = rc.getLocation().directionTo(location);
        if (rc.canMove(direction)) {
            rc.move(direction);
        } else {
            for (Direction otherDirection : Direction.allDirections()) {
                if (rc.canMove(otherDirection)) {
                    rc.move(otherDirection);
                    break;
                }
            }
        }
    }
    public static void lookForCrumb(RobotController rc) throws GameActionException {
        MapLocation [] closeByCrumbs = rc.senseNearbyCrumbs(-1);
        if(closeByCrumbs != null && closeByCrumbs.length > 0) {
            Direction crumbDir = rc.getLocation().directionTo(closeByCrumbs[0]);
            if(rc.canMove(crumbDir)) rc.move(crumbDir);
        }
        if(rc.isMovementReady()){
            if(rc.isMovementReady()){
                if(direction !=null &&rc.canMove(direction))rc.move(direction);
            }
            else{
                direction = Direction.allDirections()[RobotPlayer.rng.nextInt(Direction.allDirections().length)];
            }
        }
    }
}
