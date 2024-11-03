package Team3;

import battlecode.common.*;
public class HealerDuck extends Duck {

    private static final int MAX_HEALTH_THRESHOLD = 300;

    public HealerDuck(RobotController rc) {
        super(rc);
        skill = SkillType.HEAL;
    }

    // this method will return true / false based on the fact if it is healing or not. this return can be utilized
    // to take a move action upon not healing.

    public void exploreAround() throws GameActionException {
        MapLocation[] closeByCrumbs = rc.senseNearbyCrumbs(-1);
        while (closeByCrumbs.length > 0) {
            moveToward(closeByCrumbs[0]);
            closeByCrumbs = rc.senseNearbyCrumbs(-1);
        }
    }

    @Override
    public void play() throws GameActionException {
        if (!heal()) {  // Try to heal first, and only proceed if no healing was done
            lookForFlag();
            exploreAround();
            move();
        }
    }

    private boolean heal() throws GameActionException {
        // heal () should be called from move method.
        // sensing all the robots near in its vision to heal. it will heal only the ally robots.
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(-1, rc.getTeam());
        boolean didHeal = false;
        for (RobotInfo ally : nearbyAllies) {
            // need to find the constants and replace 100 with that HP constants (better not to use hardcode value)
            if (ally.getHealth() <= MAX_HEALTH_THRESHOLD) {
                // Heal the ally if it's within healing range
                if (rc.canHeal(ally.location)) {
                    rc.heal(ally.location);
<<<<<<< HEAD
                    // add experience while healing.
                    rc.getExperience(skill);
                    didHeal =  true;  // Heal only one ally per turn
=======
                    didHeal = true;  // Heal only one ally per turn
>>>>>>> dev
                    break;
                }
            }
        }
        return didHeal;
    }

    public void move() throws GameActionException {
        if (rc.hasFlag()) {
            moveToward(allySpawnZoneDirection());
        } else {
            moveInRandomDirection();
        }
    }
}
