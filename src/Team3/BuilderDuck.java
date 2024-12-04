package Team3;

import battlecode.common.*;

import java.util.Arrays;

/**
 * BuilderDuck class to defend flags, place traps,
 * adapt terrain, and maintain resources.
 */
public final class BuilderDuck extends Duck {
    /**
     * Threshold.
     */
    public static final int FOUR = 4;

    /**
     * Constructor for BuilderDuck.
     *
     * @param rc
     */
    public BuilderDuck(final RobotController rc) {
        super(rc, SkillType.BUILD);
    }

    @Override
    public boolean play() throws GameActionException {
        boolean playedSuccessfully = false;
        try {
            super.setupPlay();

            // Defend flags by staying close and placing traps
            defendFlag();

            // Place traps strategically around the flag or critical locations
            placeTraps();

            // Adapt terrain to address water obstacles
            handleWaterObstacles();

            // Collect crumbs to maintain a supply for building and defending
            collectCrumbs();

            // Move if no specific task is immediately required
            moveInRandomDirection();

            playedSuccessfully = true;
        } catch (GameActionException e) {
        }
        return playedSuccessfully;
    }

    /**
     * Defend the flag by staying near it and positioning traps.
     *
     * @return True if BuilderDuck stayed near flag, False otherwise
     * @throws GameActionException
     */
    public boolean defendFlag() throws GameActionException {
        RobotController rc = getRobotController();
        boolean movedTowardFlag = false;
        try {
            MapLocation flagLocation = Arrays
                    .stream(rc.senseNearbyFlags(
                            GameConstants.VISION_RADIUS_SQUARED, rc.getTeam()))
                    .map(flag -> flag.getLocation())
                    .findFirst().get();

            if (rc.getLocation().distanceSquaredTo(flagLocation) > FOUR) {
                movedTowardFlag = moveToward(flagLocation);
            }
        } catch (Exception e) {
        }
        return movedTowardFlag;
    }

    /**
     * Place traps to defend flags or key locations.
     *
     * @return True if BuilderDuck built a trap, False otherwise
     * @throws GameActionException
     */
    public boolean placeTraps() throws GameActionException {
        RobotController rc = getRobotController();
        MapLocation currentLocation = rc.getLocation();

        for (TrapType trapType : trapTypes()) {
            for (Direction direction : randomDirections()) {
                MapLocation targetLocation = currentLocation.add(direction);
                if (rc.canBuild(trapType, targetLocation)) {
                    rc.build(TrapType.EXPLOSIVE, targetLocation);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Handles water obstacles by filling tiles.
     *
     * @return True if BuilderDuck fills square, False otherwise
     * @throws GameActionException
     */
    public boolean handleWaterObstacles() throws GameActionException {
        RobotController rc = getRobotController();
        boolean didFill = false;

        try {
            MapLocation targetLocation = randomDirections()
                    .stream()
                    .filter(dir -> rc.canFill(rc.getLocation().add(dir)))
                    .map(dir -> rc.getLocation().add(dir))
                    .findFirst().get();

            rc.fill(targetLocation);
            didFill = true;
        } catch (Exception e) {
        }

        return didFill;
    }
}
