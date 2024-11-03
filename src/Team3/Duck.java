package Team3;

import battlecode.common.*;

import java.util.*;

public class Duck {
    RobotController rc;
    SkillType skill;

    public Duck(RobotController rc) {
        this.rc = rc;
    }

    public void updateEnemyRobots() throws GameActionException {
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

    public void setupPlay() throws GameActionException {
        if (!rc.isSpawned()) {
            RobotPlayer.spawn(rc);
        }
    }

    public void play() throws GameActionException {
        lookForFlag();

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
        updateEnemyRobots();
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

    public boolean moveAwayFrom(MapLocation location) throws GameActionException {
        Direction direction = rc.getLocation().directionTo(location).opposite();
        return moveToward(direction);
    }

    public boolean moveToward(MapLocation location) throws GameActionException {
        Direction direction = rc.getLocation().directionTo(location);
        return moveToward(direction);
    }

    public boolean moveToward(Direction direction) throws GameActionException {
        boolean didMove = false;
        if (rc.canFill(rc.getLocation().add(direction))) {
            rc.fill(rc.getLocation().add(direction));
        }
        if (rc.canMove(direction)) {
            didMove = true;
            rc.move(direction);
        }
        return didMove;
    }

    public static ArrayList<Direction> randomDirections() {
        ArrayList<Direction> directions = new ArrayList(Arrays.asList(Direction.allDirections()));
        Collections.shuffle(directions);
        return directions;
    }

    public boolean moveInRandomDirection() throws GameActionException {
        boolean didMove = false;
        for (Direction dir : randomDirections()) {
            didMove = moveToward(dir);
            if (didMove) break;
        }
        return didMove;
    }

    public Direction allySpawnZoneDirection() {
        ArrayList<MapLocation> allySpawnLocations = new ArrayList(Arrays.asList(rc.getAllySpawnLocations()));
        Collections.shuffle(allySpawnLocations);
        return rc.getLocation().directionTo(allySpawnLocations.get(0));
    }

    public Direction enemySpawnZoneDirection() {
        return allySpawnZoneDirection().opposite();
    }
}
