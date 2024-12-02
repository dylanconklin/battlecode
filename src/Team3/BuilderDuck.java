package Team3;

import battlecode.common.*;

import java.util.ArrayList;

/**
 * BuilderDuck class to defend flags, place traps, adapt terrain, and maintain resources.
 */
public class BuilderDuck extends Duck {

    public BuilderDuck(RobotController rc) {
        super(rc, SkillType.BUILD);
    }

    @Override
    public void play() throws GameActionException {
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
        move();
    }

    /**
     * Defend the flag by staying near it and positioning traps.
     * @throws GameActionException
     */
    public void defendFlag() throws GameActionException {
        RobotController rc = getRobotController();
        FlagInfo[] flags = rc.senseNearbyFlags(GameConstants.VISION_RADIUS_SQUARED, rc.getTeam());
        for (FlagInfo flag : flags) {
            if (rc.getLocation().distanceSquaredTo(flag.getLocation()) > 4) {
                moveToward(flag.getLocation());
                break;
            }
        }
    }

    /**
     * Place traps to defend flags or key locations.
     * @throws GameActionException
     */
    public void placeTraps() throws GameActionException {
        RobotController rc = getRobotController();
        MapLocation currentLocation = rc.getLocation();
        ArrayList<Direction> directions = randomDirections();

        for (Direction direction : directions) {
            MapLocation targetLocation = currentLocation.add(direction);

            // Try placing traps in the priority order: Explosive, Stun, Water
            if (rc.canBuild(TrapType.EXPLOSIVE, targetLocation)) {
                rc.build(TrapType.EXPLOSIVE, targetLocation);
                break;
            } else if (rc.canBuild(TrapType.STUN, targetLocation)) {
                rc.build(TrapType.STUN, targetLocation);
                break;
            } else if (rc.canBuild(TrapType.WATER, targetLocation)) {
                rc.build(TrapType.WATER, targetLocation);
                break;
            }
        }
    }

    /**
     * Handles water obstacles by filling tiles.
     * @throws GameActionException
     */
    public void handleWaterObstacles() throws GameActionException {
        RobotController rc = getRobotController();
        ArrayList<Direction> directions = randomDirections();

        for (Direction direction : directions) {
            MapLocation targetLocation = rc.getLocation().add(direction);
            if (rc.canFill(targetLocation)) {
                rc.fill(targetLocation);
                break;
            }
        }
    }

    /**
     * Collect crumbs to maintain a supply for building and defending.
     * @return True if Duck moved, False otherwise
     * @throws GameActionException
     */
    @Override
    public boolean collectCrumbs() throws GameActionException {
        RobotController rc = getRobotController();
        MapLocation[] crumbLocations = rc.senseNearbyCrumbs(GameConstants.VISION_RADIUS_SQUARED);
        boolean didMove = false;
        if (crumbLocations.length > 0) {
            didMove = moveToward(crumbLocations[0]);
        } else {
            didMove = moveInRandomDirection();
        }
        return didMove;
    }

    /**
     * Move towards important areas or explore randomly.
     * @throws GameActionException
     */
    public void move() throws GameActionException {
        RobotController rc = getRobotController();

        // If no immediate tasks are required, move randomly
        if (!moveInRandomDirection()) {
            Direction randomDirection = RobotPlayer.DIRECTIONS[RobotPlayer.RNG.nextInt(RobotPlayer.DIRECTIONS.length)];
            if (rc.canMove(randomDirection)) {
                rc.move(randomDirection);
            }
        }
    }
}
