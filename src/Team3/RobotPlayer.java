package Team3;

import battlecode.common.*;

import java.util.Random;

/**
 * RobotPlayer is the class that describes your main robot strategy.
 * The run() method inside this class is like your main function: this is what we'll call once your robot
 * is created!
 */
public strictfp class RobotPlayer {
    static final Random rng = new Random(6147);
    // Array containing all the possible movement directions.
    static final Direction[] directions = {Direction.NORTH, Direction.NORTHEAST, Direction.EAST, Direction.SOUTHEAST, Direction.SOUTH, Direction.SOUTHWEST, Direction.WEST, Direction.NORTHWEST,};
    static int turnCount = 0;

    // Make sure you spawn your robot in before you attempt to take any actions!
    // Robots not spawned in do not have vision of any tiles and cannot perform any actions.
    public static void spawn(RobotController rc) throws GameActionException {
        while (!rc.isSpawned()) {
            MapLocation[] spawnLocs = rc.getAllySpawnLocations();
            // Pick a random spawn location to attempt spawning in.
            MapLocation randomLoc = spawnLocs[rng.nextInt(spawnLocs.length)];
            if (rc.canSpawn(randomLoc)) rc.spawn(randomLoc);
        }
    }

    /**
     * run() is the method that is called when a robot is instantiated in the Battle code world.
     * It is like the main function for your robot. If this method returns, the robot dies!
     *
     * @param rc The RobotController object. You use it to perform actions from this robot, and to get information on its current status. Essentially your portal to interacting with the world.
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) {
        while (true) {
            turnCount++;

            try {
                spawn(rc);
                if (rc.canPickupFlag(rc.getLocation())) {
                    rc.pickupFlag(rc.getLocation());
                }
                // If we are holding an enemy flag, singularly focus on moving towards an ally spawn zone to capture it! We use the check roundNum >= SETUP_ROUNDS to make sure setup phase has ended.
                while (rc.hasFlag() && rc.getRoundNum() >= GameConstants.SETUP_ROUNDS) {
                    moveTowardAllySpawnZone(rc);
                }
                // Move and attack randomly if no objective.
                Direction dir = directions[rng.nextInt(directions.length)];
                MapLocation nextLoc = rc.getLocation().add(dir);
                if (rc.canMove(dir)) {
                    rc.move(dir);
                } else if (rc.canAttack(nextLoc)) {
                    rc.attack(nextLoc);
                }

                // Rarely attempt placing traps behind the robot.
                MapLocation prevLoc = rc.getLocation().subtract(dir);
                if (rc.canBuild(TrapType.EXPLOSIVE, prevLoc) && rng.nextInt() % 37 == 1)
                    rc.build(TrapType.EXPLOSIVE, prevLoc);
                // We can also move our code into different methods or classes to better organize it!
                updateEnemyRobots(rc);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Clock.yield();
            }
        }
    }

    private static void moveTowardAllySpawnZone(RobotController rc) throws GameActionException {
        MapLocation[] spawnLocs = rc.getAllySpawnLocations();
        MapLocation firstLoc = spawnLocs[0];
        Direction dir = rc.getLocation().directionTo(firstLoc);
        if (rc.canMove(dir)) rc.move(dir);
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
}
