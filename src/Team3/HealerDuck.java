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

    public int exploreAround() throws GameActionException {
        MapLocation[] closeByCrumbs = rc.senseNearbyCrumbs(-1);
        if (closeByCrumbs != null && closeByCrumbs.length > 0) {
            while (closeByCrumbs.length > 0) {
            moveToward(Direction.allDirections()[0]);
            closeByCrumbs = rc.senseNearbyCrumbs(-1);
            return 1;
        }
        }else{
            return 0;
        }
    return 0;
    }

    @Override
    public void play() throws GameActionException {
        super.setupPlay();
        if (!heal()) {  // Try to heal first, and only proceed if no healing was done
            lookForFlag();
            exploreAround();
            move();
        }
    }

    public boolean heal() throws GameActionException {
        // heal () should be called from move method.
        // sensing all the robots near in its vision to heal. it will heal only the ally robots.
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(-1, rc.getTeam());
        boolean didHeal = false;
        if(nearbyAllies != null && nearbyAllies.length > 0) {
            for (RobotInfo ally : nearbyAllies) {
            // need to find the constants and replace 100 with that HP constants (better not to use hardcode value)
            if (ally.getHealth() <= MAX_HEALTH_THRESHOLD) {
                // Heal the ally if it's within healing range
                if (rc.canHeal(ally.location)) {
                    rc.heal(ally.location);
                    didHeal = true;  // Heal only one ally per turn
                    break;
                }
            }
        }}
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
