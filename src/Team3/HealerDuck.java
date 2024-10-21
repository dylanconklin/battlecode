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

    // this method will return true / false based on the fact if it is healing or not. this return can be utilized
    // to take a move action upon not healing.

    private boolean heal() throws GameActionException {
        // heal () should be called from move method.
        //sensing all the robots near in its vision to heal. it will heal only the ally robots.
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(-1,rc.getTeam());
        for (RobotInfo ally : nearbyAllies) {
            // need to find the constants and replace 100 with that HP constants (better not to use hardcode value)
            if ( ally.getHealth() < 100) {
                // Heal the ally if it's within healing range
                if (rc.canHeal(ally.location)) {
                    rc.heal(ally.location);
                    // add experience while healing.
                    rc.getExperience(skill);
                    return true;  // Heal only one ally per turn
                }
            }
        }
        return false;
    }
    public void move() throws GameActionException {
        boolean healed = heal();
        if (!healed) {
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
        }u

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
