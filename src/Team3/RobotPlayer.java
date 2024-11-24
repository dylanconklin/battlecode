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
    //public Logger l = Logger.getInstance("debuglog.txt");
    // Make sure you spawn your robot in before you attempt to take any actions!
    // Robots not spawned in do not have vision of any tiles and cannot perform any actions.
    public static Duck spawn(RobotController rc) throws GameActionException {
        while (!rc.isSpawned()) {
            MapLocation[] spawnLocs = rc.getAllySpawnLocations();
            // Pick a random spawn location to attempt spawning in.
            MapLocation randomLoc = spawnLocs[rng.nextInt(spawnLocs.length)];
            if (rc.canSpawn(randomLoc)) rc.spawn(randomLoc);
        }
        int choice = rng.nextInt(3);
        if (choice == 0) {
            return new AttackerDuck(rc);
        } else if (choice == 1) {
            return new HealerDuck(rc);
        } else {
            return new BuilderDuck(rc);
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
        Duck duck = null;
        try {
            duck = spawn(rc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (true) {
            turnCount++;
            try {
                assert duck != null;
                duck.play();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Clock.yield();
            }
        }
    }
}
