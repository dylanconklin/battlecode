package Team3;

import battlecode.common.*;

import java.util.Arrays;
import java.util.Comparator;

/**
 * The HealerDuck class extends the Duck class and provides healing functionality to nearby allies.
 * This class implements logic for healing, moving, and attacking during gameplay.
 */
public final class HealerDuck extends Duck {

    /** The health threshold below which healing should be prioritized. */
    private static final int MAX_HEALTH_THRESHOLD = 900;

    /** The team this robot belongs to. */
    private Team myTeam = null;

    private Team opTeam = null;

    /**
     * Constructor for HealerDuck.
     *
     * @param rc The RobotController instance for the robot.
     */
    public HealerDuck(RobotController rc) {
        super(rc, SkillType.HEAL);
        myTeam = rc.getTeam();
        opTeam = rc.getTeam().opponent();
    }


    /**
     * Explores nearby areas and collects crumbs if present.
     *
     * @return The number of crumbs found and collected.
     * @throws GameActionException if an error occurs during exploration.
     */
    public int exploreAround() throws GameActionException {
        RobotController rc = getRobotController();
        int found_crumbs = 0 ;
        MapLocation[] closeByCrumbs = rc.senseNearbyCrumbs(-1);
        if (closeByCrumbs.length > 0) {
            moveToward(closeByCrumbs[0]);
            closeByCrumbs = rc.senseNearbyCrumbs(-1);
            found_crumbs++;
        }
        return found_crumbs;
    }

    /**
     * Executes the main behavior of the HealerDuck, including healing, moving, and attacking.
     *
     * @return True if the behavior was executed successfully, false otherwise.
     * @throws GameActionException if an error occurs during gameplay.
     */
    @Override
    public boolean play() throws GameActionException {
        boolean playedSuccessfully = false;
        try {
            RobotController rc = getRobotController();
            super.setupPlay();
            MapLocation ml = rc.getLocation();
            if (!heal_ally()) {  // Try to heal first, and only proceed if no healing was done
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
     * Finds the most appropriate target for healing based on priority and health level.
     *
     * @param c The center location to search for heal targets.
     * @param r The radius within which to search for heal targets.
     * @return The RobotInfo of the best target to heal, or null if no target is found.
     * @throws GameActionException if an error occurs during sensing.
     */
    private RobotInfo healTarget(MapLocation c,int r) throws GameActionException {
        RobotController rc = getRobotController();
        RobotInfo target = null;
        int minHealth = Integer.MAX_VALUE;
        int maxPriority = Integer.MIN_VALUE;

        RobotInfo[] robots = rc.senseNearbyRobots(c, r, myTeam);
        if (robots == null  ) return null;
        for (int i = robots.length; --i >= 0; ) {
            RobotInfo robot = robots[i];
            if (robot.health == GameConstants.DEFAULT_HEALTH) {
                continue;
            }

            int priority = robot.attackLevel + robot.healLevel + robot.buildLevel;
            if (robot.hasFlag) {
                priority += 1000;
            }

            if (target == null || priority > maxPriority || (priority == maxPriority && robot.health < minHealth)) {
                target = robot;
                minHealth = robot.health;
                maxPriority = priority;
            }
        }
        return target;
    }

    /**
     * Attempts to heal a nearby ally.
     *
     * @return True if healing was performed, false otherwise.
     * @throws GameActionException if an error occurs during healing.
     */
    public boolean heal_ally() throws GameActionException {
        RobotController rc = getRobotController();
        // heal () is only done if no opponent in vision_radius.
        if (!rc.isActionReady() || rc.senseNearbyRobots(GameConstants.VISION_RADIUS_SQUARED,rc.getTeam().opponent() ).length > 0) {
            return false;
        }
        boolean didHeal = false;
        int maxScore = Integer.MIN_VALUE;
        // sensing all the robots near in its vision to heal. it will heal only the ally robots.
        Direction bestDirection = null;
        RobotInfo bestTarget = null;
        MapLocation ml = rc.getLocation();
        Direction[] allDirections = Direction.values();
        for (int i = allDirections.length; --i >= 0; ) {
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
                score += 1000;
            }

            if (score > maxScore) {
                bestDirection = direction;
                bestTarget = newTarget;
                maxScore = score;
            }
        }
        if (bestDirection != null) {
            if (bestDirection != Direction.CENTER) {

                rc.move(bestDirection);
            }

            if (rc.canHeal(bestTarget.location)) {
                int a_heal_lvl = bestTarget.getHealth();
                rc.heal(bestTarget.location);

                rc.getExperience(getSkill());
                didHeal = true;
            }
        }


        return didHeal;
    }


    /**
     * Handles movement logic for the HealerDuck, prioritizing healing targets and objectives.
     *
     * @throws GameActionException if an error occurs during movement.
     */

    public void move() throws GameActionException {
        RobotController rc = getRobotController();
        RobotInfo[] ducksToHeal = Arrays.stream(rc.senseNearbyRobots())
                .filter(robot -> robot.getTeam() == rc.getTeam() && robot.getHealth() <= 300)
                .sorted(Comparator.comparing(r -> r.getHealth()))
                .toArray(RobotInfo[]::new);
        if (ducksToHeal.length > 0) {
            moveToward(ducksToHeal[0].getLocation());
        } else if (rc.hasFlag()) {
            moveToward(allySpawnZoneDirection());
        } else if (rc.getRoundNum() <= GameConstants.SETUP_ROUNDS) {
            moveToward(enemySpawnZoneDirection());
        } else {
            moveInRandomDirection();
        }
    }

    /**
     * Handles the attacking behavior of the HealerDuck, targeting enemies in range.
     *
     * @return The number of enemy robots attacked.
     * @throws GameActionException if an error occurs during attack.
     */
    public int attack() throws GameActionException {
        RobotController rc = getRobotController();
        RobotInfo[] robotInfos = rc.senseNearbyRobots(GameConstants.VISION_RADIUS_SQUARED,opTeam);
        for (RobotInfo robot : robotInfos) {
            if (rc.canAttack(robot.location)) {
                rc.attack(robot.location);
            }
        }
        return robotInfos.length;
    }
}
