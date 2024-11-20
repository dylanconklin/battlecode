package Team3;

import battlecode.common.*;
public class HealerDuck extends Duck {

    //total health is 1000 and healing to be done when life drops below 900.
    private static final int MAX_HEALTH_THRESHOLD = 900;

    private Team myTeam = null;
    private Team opTeam = null;

    public HealerDuck(RobotController rc) {
        super(rc);
        myTeam = rc.getTeam();
        opTeam = rc.getTeam().opponent();
        skill = SkillType.HEAL;
        System.out.println("DBG: HealerDuck");
    }

    // this method will return true / false based on the fact if it is healing or not. this return can be utilized

    public int exploreAround() throws GameActionException {
        int found_crumbs = 0 ;
        MapLocation[] closeByCrumbs = rc.senseNearbyCrumbs(-1);
        while (closeByCrumbs.length > 0) {
            moveToward(closeByCrumbs[0]);
            closeByCrumbs = rc.senseNearbyCrumbs(-1);
            found_crumbs++;
        }
        return found_crumbs;
    }

    @Override
    public void play() throws GameActionException {
        super.setupPlay();
        MapLocation ml = rc.getLocation();
        if (!heal_ally()) {  // Try to heal first, and only proceed if no healing was done
            lookForFlag();
            exploreAround();
            move();
        }

    }
    private RobotInfo healTarget(MapLocation c,int r) throws GameActionException {
        RobotInfo target = null;
        int minHealth = Integer.MAX_VALUE;
        int maxPriority = Integer.MIN_VALUE;

        RobotInfo[] robots = rc.senseNearbyRobots(c, r, myTeam);
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

    public boolean heal_ally() throws GameActionException {
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
                System.out.println("heal move " + bestDirection);
                rc.move(bestDirection);
            }

            if (rc.canHeal(bestTarget.location)) {
                int a_heal_lvl = bestTarget.getHealth();
                rc.heal(bestTarget.location);
                System.out.println("healing from: "+a_heal_lvl +" : "+bestTarget.getHealth());
                rc.getExperience(skill);
                didHeal = true;
            }
        }


        return didHeal;
    }

    public void move() throws GameActionException {

        if (rc.getRoundNum() <= GameConstants.SETUP_ROUNDS ){
            moveToward(enemySpawnZoneDirection());
        }
        else if (rc.hasFlag()) {
            moveToward(allySpawnZoneDirection());

        } else {
            moveInRandomDirection();
        }
    }
}
