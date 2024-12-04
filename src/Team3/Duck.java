package Team3;

import battlecode.common.*;
import battlecode.world.Trap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * Duck parent class.
 */
public class Duck {
    /**
     * The chosen health threshold.
     */
    public static final int HEALTH_THRESHOLD = 300;
    /**
     * Thirty seven. No idea why this number.
     */
    public static final int THIRTYSEVEN = 37;
    /**
     * Random number generator.
     */
    private static final Random RNG = new Random();
    /**
     * The robot controller.
     */
    private final RobotController rc;
    /**
     * Duck skill type.
     */
    private final SkillType skill;

    /**
     * Duck constructer.
     *
     * @param controller
     * @param duckSkill
     */
    public Duck(final RobotController controller, final SkillType duckSkill) {
        this.rc = controller;
        this.skill = duckSkill;
    }

    /**
     * Get a randomized list of Directions.
     *
     * @return An ArrayList of Directions
     */
    public static ArrayList<Direction> randomDirections() {
        ArrayList<Direction> directions = new ArrayList<Direction>(
                Arrays.asList(Direction.allDirections()));
        directions.remove(Direction.CENTER);
        Collections.shuffle(directions);
        directions.add(Direction.CENTER);
        return directions;
    }

    static ArrayList<Direction> directions() {
        ArrayList<Direction> directions = new ArrayList<>(
                Arrays.asList(Direction.values()));
        directions.remove(Direction.CENTER);
        return directions;
    }

    static ArrayList<TrapType> trapTypes() {
        ArrayList<TrapType> trapTypes = new ArrayList<>(
                Arrays.asList(TrapType.EXPLOSIVE, TrapType.STUN, TrapType.WATER, TrapType.NONE));
        return trapTypes;
    }

    /**
     * Getter for RobotController.
     *
     * @return This Duck's RobotController
     */
    RobotController getRobotController() {
        return rc;
    }

    /**
     * Getter for skill.
     *
     * @return This Duck's Skill
     */
    SkillType getSkill() {
        return skill;
    }

    /**
     * Update the number of enemies on the board.
     *
     * @return the number of enemies on the board
     * @throws GameActionException
     */
    public int updateEnemyRobots() throws GameActionException {
        // Sensing methods can be passed in a radius of -1 to automatically
        // use the largest possible value.
        RobotInfo[] enemyRobots = rc.senseNearbyRobots(
                -1, rc.getTeam().opponent());
        if (enemyRobots.length != 0) {
            // Let the rest of our team know how many enemy robots we see!
            updateEnemiesList(enemyRobots);
        }
        return enemyRobots.length;
    }

    /**
     * Update the count of enemies.
     *
     * @param enemyRobots
     * @return number of enemies
     * @throws GameActionException
     */
    public int updateEnemiesList(final RobotInfo[] enemyRobots)
            throws GameActionException {
        // Let the rest of our team know how many enemy robots we see!
        if (rc.canWriteSharedArray(0, enemyRobots.length)) {
            rc.writeSharedArray(0, enemyRobots.length);
            int numEnemies = rc.readSharedArray(0);
        }
        return enemyRobots.length;
    }

    /**
     * Make sure Duck is spawned.
     *
     * @return True if Duck is spawned, False otherwise
     * @throws GameActionException
     */
    public boolean setupPlay() throws GameActionException {
        if (!rc.isSpawned()) {
            RobotPlayer.spawn(rc);
        }
        return rc.isSpawned();
    }

    /**
     * Decider for what move to make.
     *
     * @return True if Duck finished a turn successfully, False otherwise
     * @throws GameActionException
     */
    public boolean play() throws GameActionException {
        boolean playedSuccessfully = false;
        try {
            lookForFlag();

            while (rc.hasFlag() && rc.getRoundNum() >= GameConstants.SETUP_ROUNDS) {
                moveToward(allySpawnZoneDirection());
            }
            // Move and attack randomly if no objective.
            Direction dir = RobotPlayer.DIRECTIONS[
                    RobotPlayer.RNG.nextInt(RobotPlayer.DIRECTIONS.length)];
            MapLocation nextLoc = rc.getLocation().add(dir);
            if (rc.canMove(dir)) {
                rc.move(dir);
            } else if (rc.canAttack(nextLoc)) {
                rc.attack(nextLoc);
            }

            // Rarely attempt placing traps behind the robot.
            MapLocation prevLoc = rc.getLocation().subtract(dir);
            if (rc.canBuild(TrapType.EXPLOSIVE, prevLoc)
                    && RobotPlayer.RNG.nextInt() % THIRTYSEVEN == 1) {
                rc.build(TrapType.EXPLOSIVE, prevLoc);
            }
            // We can also move our code into different methods or classes
            // to better organize it!
            updateEnemyRobots();
            playedSuccessfully = true;
        } catch (GameActionException e) {
        }
        return playedSuccessfully;
    }

    /**
     * Look for nearby flags.
     *
     * @return True if Duck picked up flag, False otherwise
     * @throws GameActionException
     */
    public boolean lookForFlag() throws GameActionException {
        boolean pickedUpFlag = false;
        FlagInfo[] flags = rc.senseNearbyFlags(-1, rc.getTeam());
        for (FlagInfo flag : flags) {
            if (rc.canPickupFlag(flag.getLocation())) {
                pickedUpFlag = true;
                rc.pickupFlag(flag.getLocation());
                break;
            }
        }
        return pickedUpFlag;
    }

    /**
     * Move away from Direction.
     *
     * @param location
     * @return True if Duck moved, False otherwise
     * @throws GameActionException
     */
    public boolean moveAwayFrom(final MapLocation location)
            throws GameActionException {
        Direction direction = rc.getLocation().directionTo(location).opposite();
        return moveToward(direction);
    }

    /**
     * Move in a random direction.
     *
     * @return True if Duck moved, False otherwise
     * @throws GameActionException
     */
    public boolean moveInRandomDirection() throws GameActionException {
        boolean didMove = false;
        for (Direction dir : randomDirections()) {
            didMove = moveToward(dir);
            if (didMove) {
                break;
            }
        }
        return didMove;
    }

    /**
     * Get direction toward ally spawn zone.
     *
     * @return Direction toward ally spawn zone
     */
    public Direction allySpawnZoneDirection() {
        ArrayList<MapLocation> allySpawnLocations = new ArrayList<MapLocation>(
                Arrays.asList(rc.getAllySpawnLocations()));
        Collections.shuffle(allySpawnLocations);
        return rc.getLocation().directionTo(allySpawnLocations.get(0));
    }

    /**
     * Get direction toward enemy spawn zone.
     *
     * @return Direction toward enemy spawn zone
     */
    public Direction enemySpawnZoneDirection() {
        return allySpawnZoneDirection().opposite();
    }

    /**
     * Move toward ideal location.
     *
     * @param location
     * @return True if Duck moved, False otherwise
     * @throws GameActionException
     */
    public boolean moveToward(final MapLocation location)
            throws GameActionException {
        Direction direction = rc.getLocation().directionTo(location);
        return moveToward(direction);
    }

    /**
     * Move toward ideal direction.
     *
     * @param direction
     * @return True if Duck moved, False otherwise
     * @throws GameActionException
     */
    public boolean moveToward(final Direction direction)
            throws GameActionException {
        boolean didMove = false;
        for (Direction prioritizedDirection
                : getPrioritizedDirections(direction)) {
            if (rc.canMove(prioritizedDirection)) {
                rc.move(prioritizedDirection);
                didMove = true;
                break;
            }
        }
        return didMove;
    }

    /**
     * Collect nearby crumbs.
     *
     * @return True if Duck moved, False otherwise
     * @throws GameActionException
     */
    public boolean collectCrumbs() throws GameActionException {
        MapLocation[] crumbLocations =
                rc.senseNearbyCrumbs(GameConstants.VISION_RADIUS_SQUARED);
        boolean didMove = false;
        if (crumbLocations.length > 0) {
            didMove = moveToward(crumbLocations[0]);
        } else {
            didMove = moveInRandomDirection();
        }
        return didMove;
    }

    // This returns an array of all possible directions in order of priority
    // based on the seed direction(primaryDirection)
    private ArrayList<Direction> getPrioritizedDirections(
            final Direction primaryDirection) throws GameActionException {
        ArrayList<Direction> result = new ArrayList<>();
        ArrayList<Direction> directions = directions();
        int direction = directions.indexOf(primaryDirection);
        int moveCounter = 0;
        result.add(primaryDirection);
        while (moveCounter * 2 < directions.size()) {
            // Randomize whether we're going left or right,
            // then add both directions
            direction *= RNG.nextBoolean() ? 1 : -1;
            result.add(directions.get(direction < 0
                    ? direction + directions.size()
                    : direction % directions.size()));
            direction *= -1;
            result.add(directions.get(direction < 0
                    ? direction + directions.size()
                    : direction % directions.size()));
            moveCounter++;
        }
        result.add(primaryDirection.opposite());
        result.add(Direction.CENTER);
        return result;
    }
}
