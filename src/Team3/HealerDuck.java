package Team3;

import battlecode.common.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.stream.Collectors;

public final class HealerDuck extends Duck {

    /**
     * Total health is 1000 and healing to be done when life drops below 900.
     */
    private static final int MAX_HEALTH_THRESHOLD = 900;
    /**
     * One Thousand.
     */
    private static final int ONE_THOUSAND = 1000;

    /**
     * Constructor for HealerDuck.
     * @param rc
     */
    public HealerDuck(final RobotController rc) {
        super(rc, SkillType.HEAL);
    }

    /**
     * Return True if crumb is found, False otherwise.
     * @return Return True if crumb is found, False otherwise.
     * @throws GameActionException
     */
    public boolean exploreAround() throws GameActionException {
        boolean foundCrumbs = false;
        try {
            MapLocation closeByCrumb = Arrays
                    .stream(getRobotController().senseNearbyCrumbs(-1))
                    .findFirst().get();
            moveToward(closeByCrumb);
            foundCrumbs = true;
        } catch (Exception e) {
        }
        return foundCrumbs;
    }

    /**
     * Play logic for HealerDuck
     * @return True if played successfully, False otherwise
     * @throws GameActionException
     */
    @Override
    public boolean play() throws GameActionException {
        super.setupPlay();
        RobotController rc = getRobotController();
        boolean playedSuccessfully = false;
        try {
            MapLocation ml = rc.getLocation();
            // Try to heal first, and only proceed if no healing was done
            if (!healAlly()) {
                lookForFlag();
                exploreAround();
                attack();
                move();
            }
            playedSuccessfully = true;
        } catch (GameActionException e) {
        }
        return playedSuccessfully;
    }

    /**
     * Get most important Duck to heal.
     * @param c
     * @param r
     * @return Top priority Duck, null otherwise
     * @throws GameActionException
     */
    private RobotInfo healTarget(final MapLocation c, final int r)
            throws GameActionException {
        RobotController rc = getRobotController();
        RobotInfo target = null;
        int minHealth = Integer.MAX_VALUE;
        int maxPriority = Integer.MIN_VALUE;

        try {
            target = Arrays
                    .stream(rc.senseNearbyRobots(c, r, rc.getTeam()))
                    .filter(robot -> robot.health != GameConstants.DEFAULT_HEALTH)
                    .sorted(Comparator.comparing(
                            robot -> robot.attackLevel
                                    + robot.healLevel
                                    + robot.buildLevel
                                    + (robot.hasFlag() ? ONE_THOUSAND : 0)))
                    .findFirst().get();
        } catch (Exception e) {
        }
        return target;
    }

    public boolean healAlly() throws GameActionException {
        RobotController rc = getRobotController();
        // heal () is only done if no opponent in vision_radius.
        if (!rc.isActionReady() || rc.senseNearbyRobots(GameConstants.VISION_RADIUS_SQUARED, rc.getTeam().opponent()).length > 0) {
            return false;
        }
        boolean didHeal = false;
        int maxScore = Integer.MIN_VALUE;
        // sensing all the robots near in its vision to heal. it will heal only the ally robots.
        Direction bestDirection = null;
        RobotInfo bestTarget = null;
        MapLocation ml = rc.getLocation();
        Direction[] allDirections = Direction.values();
        for (int i = allDirections.length; --i >= 0;) {
            Direction direction = allDirections[i];
            if (direction != Direction.CENTER && !rc.canMove(direction)) {
                continue;
            }

            MapLocation newLocation = rc.adjacentLocation(direction);
            RobotInfo newTarget = healTarget(newLocation, GameConstants.HEAL_RADIUS_SQUARED);
            if (newTarget == null) {
                continue;
            }


            int score = newTarget.attackLevel + newTarget.healLevel + newTarget.buildLevel;
            if (newTarget.hasFlag) {
                score += ONE_THOUSAND;
            }

            if (score > maxScore) {
                bestDirection = direction;
                bestTarget = newTarget;
                maxScore = score;
            }
        }
        if (bestDirection != null) {
            if (bestDirection != Direction.CENTER) {
                System.out.println("heal move " + bestDirection);
                rc.move(bestDirection);
            }

            if (rc.canHeal(bestTarget.location)) {
                int aHealLvl = bestTarget.getHealth();
                rc.heal(bestTarget.location);
                System.out.println("healing from: " + aHealLvl + " : " + bestTarget.getHealth());
                rc.getExperience(getSkill());
                didHeal = true;
            }
        }

        return didHeal;
    }

    /**
     * Move logic for HealerDuck.
     * @return True if HealerDuck moved, False otherwise
     * @throws GameActionException
     */
    public boolean move() throws GameActionException {
        RobotController rc = getRobotController();
        boolean didMove = false;
        RobotInfo[] ducksToHeal = Arrays
                .stream(rc.senseNearbyRobots(-1, rc.getTeam()))
                .filter(robot -> robot.getHealth() <= HEALTH_THRESHOLD)
                .sorted(Comparator.comparing(r -> r.getHealth()))
                .toArray(RobotInfo[]::new);
        if (ducksToHeal.length > 0) {
            didMove = moveToward(ducksToHeal[0].getLocation());
        } else if (rc.hasFlag()) {
            didMove = moveToward(allySpawnZoneDirection());
        } else if (rc.getRoundNum() <= GameConstants.SETUP_ROUNDS) {
            didMove = moveToward(enemySpawnZoneDirection());
        } else {
            didMove = moveInRandomDirection();
        }
        return didMove;
    }
}
