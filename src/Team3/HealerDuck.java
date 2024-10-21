package Team3;

import battlecode.common.*;

public class HealerDuck extends Duck {
    static Direction direction;

    public HealerDuck(RobotController rc) throws GameActionException {
        super(rc);
        skill = SkillType.HEAL;
    }

    public void lookForFlag() throws GameActionException {
        FlagInfo[] flags = rc.senseNearbyFlags(-1, rc.getTeam());
        for (FlagInfo flag : flags) {
            if (rc.canPickupFlag(flag.getLocation())) {
                rc.canPickupFlag(flag.getLocation());
                break;
            }
        }
    }

    private void moveTowardEnemySpawnZone() throws GameActionException {
        MapLocation[] spawnLocs = rc.getAllySpawnLocations();
        MapLocation firstLoc = spawnLocs[0];
        Direction dir = rc.getLocation().directionTo(firstLoc).opposite();
        if (rc.canMove(dir)) rc.move(dir);
    }

    // this method will return true / false based on the fact if it is healing or not. this return can be utilized
    // to take a move action upon not healing.

    public void exploreAround() throws GameActionException {
        MapLocation[] closeByCrumbs = rc.senseNearbyCrumbs(-1);
        if (closeByCrumbs != null && closeByCrumbs.length > 0) {
            Direction crumbDir = rc.getLocation().directionTo(closeByCrumbs[0]);
            if (rc.canMove(crumbDir)) rc.move(crumbDir);
        }
        if (rc.isMovementReady()) {
            if (rc.isMovementReady()) {
                if (direction != null && rc.canMove(direction)) rc.move(direction);
            } else {
                direction = Direction.allDirections()[RobotPlayer.rng.nextInt(Direction.allDirections().length)];
            }
        }
    }

    @Override
    public void play() throws GameActionException {
        lookForFlag();
        exploreAround();
        move();
    }

    private boolean heal() throws GameActionException {
        // heal () should be called from move method.
        // sensing all the robots near in its vision to heal. it will heal only the ally robots.
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(-1, rc.getTeam());
        boolean didHeal = false;
        for (RobotInfo ally : nearbyAllies) {
            // need to find the constants and replace 100 with that HP constants (better not to use hardcode value)
            if (ally.getHealth() <= 300) {
                // Heal the ally if it's within healing range
                if (rc.canHeal(ally.location)) {
                    rc.heal(ally.location);
                    didHeal = true;  // Heal only one ally per turn
                }
            }
        }
        return didHeal;
    }

    public void move() throws GameActionException {
        MapLocation[] locations = rc.getAllySpawnLocations();
        if (rc.hasFlag()) {
            moveTowardAllySpawnZone();
        } else {
            // move toward adversary spawn locations
            // TODO: don't move blindly away from locations[0]
            lookForFlag(); // Look For Flag
        }
    }

    public void moveToward(MapLocation location) throws GameActionException {
        direction = rc.getLocation().directionTo(location);
        if (rc.hasFlag()) {
            moveTowardAllySpawnZone();
        } else if (rc.canMove(direction)) {
            rc.move(direction);
        } else if (rc.canFill(rc.getLocation().add(direction))) {
            rc.fill(rc.getLocation().add(direction));
        } else {
            for (Direction otherDirection : Direction.allDirections()) {
                if (rc.canMove(otherDirection)) {
                    rc.move(otherDirection);
                    break;
                }
            }
        }
    }
}
