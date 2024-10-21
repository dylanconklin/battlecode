package Team3;

import battlecode.common.*;

public class HealerDuck extends Duck {
    static Direction direction;
    public HealerDuck(RobotController rc)  throws GameActionException {
        super(rc);
        skill = SkillType.HEAL;
        play();
    }

    @Override public void play() throws GameActionException {
        lookForFlag(rc);
        exploreAround(rc);
        move();
    }
    public static void lookForFlag(RobotController rc) throws GameActionException {
        FlagInfo[]flags = rc.senseNearbyFlags(-1, rc.getTeam());
        for(FlagInfo flag : flags){
            if(rc.canPickupFlag(flag.getLocation())){
                rc.canPickupFlag(flag.getLocation());
                break;
            }
        }
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
                    rc.getExperience(SkillType.HEAL);
                    return true;  // Heal only one ally per turn
                }
            }
        }
        return false;
    }
    private static void moveTowardAllySpawnZone(RobotController rc) throws GameActionException {
        MapLocation[] spawnLocs = rc.getAllySpawnLocations();
        MapLocation firstLoc = spawnLocs[0];
        Direction dir = rc.getLocation().directionTo(firstLoc);
        if (rc.canMove(dir)) rc.move(dir);
    }
    public void move() throws GameActionException {
        MapLocation[] locations = rc.getAllySpawnLocations();
        if (rc.hasFlag()) {
            moveTowardAllySpawnZone(rc);
        } else {
            // move toward adversary spawn locations
            // TODO: don't move blindly away from locations[0]
            lookForFlag(rc); // Look For Flag
        }
    }
    private static void moveTowardEnemySpawnZone(RobotController rc) throws GameActionException {
        MapLocation[] spawnLocs = rc.getAllySpawnLocations();
        MapLocation firstLoc = spawnLocs[0];
        Direction dir = rc.getLocation().directionTo(firstLoc).opposite();
        if (rc.canMove(dir)) rc.move(dir);
    }
     public void moveToward(RobotController rc,MapLocation location) throws GameActionException {
     direction = rc.getLocation().directionTo(location);
     if(rc.hasFlag()){
       moveTowardAllySpawnZone(rc);
     }
     else if (rc.canMove(direction)) {
       rc.move(direction);
     }
     else if (rc.canFill(rc.getLocation().add(direction))) rc.fill(rc.getLocation().add(direction));
     else {
        // Direction randDirc = Direction.allDirections()[RobotPlayer.rng.nextInt(Direction.allDirections().length)];
            for (Direction otherDirection : Direction.allDirections()) {
                if (rc.canMove(otherDirection)) {
                    rc.move(otherDirection);
                    break;
                }
            }
        }
    }
    public static void exploreAround(RobotController rc) throws GameActionException {
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
