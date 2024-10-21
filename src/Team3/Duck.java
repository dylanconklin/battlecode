package Team3;

import battlecode.common.*;

public class Duck {
    RobotController rc;
    SkillType skill;

    public Duck(RobotController rc) {
        this.rc = rc;
    }

    public static void updateEnemyRobots(RobotController rc) throws GameActionException {
        // Sensing methods can be passed in a radius of -1 to automatically
        // use the largest possible value.
        RobotInfo[] enemyRobots = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        if (enemyRobots.length != 0) {
            // Save an array of locations with enemy robots in them for future use.
            MapLocation[] enemyLocations = new MapLocation[enemyRobots.length];
            for (int i = 0; i < enemyRobots.length; i++) {
                enemyLocations[i] = enemyRobots[i].getLocation();
            }
            // Let the rest of our team know how many enemy robots we see!
            if (rc.canWriteSharedArray(0, enemyRobots.length)) {
                rc.writeSharedArray(0, enemyRobots.length);
                int numEnemies = rc.readSharedArray(0);
            }
        }
    }

    public void play() throws GameActionException {
        pickupFlag();

        while (rc.hasFlag() && rc.getRoundNum() >= GameConstants.SETUP_ROUNDS) {
            moveToward(allySpawnZoneDirection());
        }
        // Move and attack randomly if no objective.
        Direction dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];
        MapLocation nextLoc = rc.getLocation().add(dir);
        if (rc.canMove(dir)) {
            rc.move(dir);
        } else if (rc.canAttack(nextLoc)) {
            rc.attack(nextLoc);
        }

        // Rarely attempt placing traps behind the robot.
        MapLocation prevLoc = rc.getLocation().subtract(dir);
        if (rc.canBuild(TrapType.EXPLOSIVE, prevLoc) && RobotPlayer.rng.nextInt() % 37 == 1)
            rc.build(TrapType.EXPLOSIVE, prevLoc);
        // We can also move our code into different methods or classes to better organize it!
        updateEnemyRobots(rc);
    }

    public void lookForFlag() throws GameActionException {
        FlagInfo[] flags = rc.senseNearbyFlags(-1, rc.getTeam());
        for (FlagInfo flag : flags) {
            if (rc.canPickupFlag(flag.getLocation())) {
                rc.pickupFlag(flag.getLocation());
                break;
            }
        }
    }

    public void moveAwayFrom(MapLocation location) throws GameActionException {
        Direction direction = rc.getLocation().directionTo(location).opposite();
        moveToward(direction);
    }

    public void moveToward(MapLocation location) throws GameActionException {
        Direction direction = rc.getLocation().directionTo(location);
        moveToward(direction);
    }

    public void moveToward(Direction direction) throws GameActionException {
        if (rc.canFill(rc.getLocation().add(direction))) {
            rc.fill(rc.getLocation().add(direction));
        }
        if (rc.canMove(direction)) {
            rc.move(direction);
        }
    }

    public Direction allySpawnZoneDirection() {
        return rc.getLocation().directionTo(rc.getAllySpawnLocations()[0]);
    }

    public Direction enemySpawnZoneDirection() {
        return allySpawnZoneDirection().opposite();
    }

    public void pickupFlag() throws GameActionException {
        if (rc.canPickupFlag(rc.getLocation())) {
            rc.pickupFlag(rc.getLocation());
        }
    }
}
