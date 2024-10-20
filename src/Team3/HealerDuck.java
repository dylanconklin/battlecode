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
        super.play();
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

    private static void moveTowardAllySpawnZone(RobotController rc) throws GameActionException {
        MapLocation[] spawnLocs = rc.getAllySpawnLocations();
        MapLocation firstLoc = spawnLocs[0];
        Direction dir = rc.getLocation().directionTo(firstLoc);
        if (rc.canMove(dir)) rc.move(dir);
    }
    public void heal() throws GameActionException {}
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
        Direction dir = rc.getLocation().directionTo(firstLoc);
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
