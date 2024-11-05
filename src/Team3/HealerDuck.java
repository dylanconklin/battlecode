package Team3;

import battlecode.common.*;
public class HealerDuck extends Duck {

    private static final int MAX_HEALTH_THRESHOLD = 300;

    public HealerDuck(RobotController rc) {
        super(rc);
        skill = SkillType.HEAL;
        System.out.println("DBG: HealDuck");
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
        MapLocation ml = rc.getLocation();
        if (ml == null)
        {
            System.out.println("DBG: H: ml is null");
            return;
        }
        heal();
        lookForFlag();
        move();
        /*
        if (!heal()) {  // Try to heal first, and only proceed if no healing was done
            lookForFlag();
            exploreAround();
            move();
        }

         */
    }

    private boolean heal() throws GameActionException {
        // heal () should be called from move method.

        // sensing all the robots near in its vision to heal. it will heal only the ally robots.

        Team t = rc.getTeam();
        MapLocation ml = rc.getLocation();
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(-1, t);

        //System.out.println("DBG: Heal starts" +nearbyAllies.length);
        boolean didHeal = false;
        if (nearbyAllies != null)
        {
            for (RobotInfo ally : nearbyAllies) {
                //System.out.println("DBG: Heal stats" +ally.getHealth());
                // need to find the constants and replace 100 with that HP constants (better not to use hardcode value)
                if (ally.getHealth() <= MAX_HEALTH_THRESHOLD) {
                    System.out.println("DBG: Heal needs" + ally.location + "can heal? " + rc.canHeal(ally.location));
                    // Heal the ally if it's within healing range
                    if (rc.canHeal(ally.location)) {
                        rc.heal(ally.location);
                        System.out.println("DBG: Heal done");
                        // add experience while healing.
                        rc.getExperience(skill);
                        didHeal = true;
                        break;
                    }
                }
            }
        }

        return didHeal;
    }

    public void move() throws GameActionException {
        if (rc.getRoundNum() >= GameConstants.SETUP_ROUNDS ) {
            // move toward ally spawn locations
            // TODO: don't move blindly toward locations[0]
            moveToward(allySpawnZoneDirection());
        } else if (rc.getRoundNum() >= GameConstants.SETUP_ROUNDS) {
            // move toward adversary spawn locations
            // TODO: don't move blindly away from locations[0]
            moveToward(enemySpawnZoneDirection());
        } else {
            moveInRandomDirection();
        }

        /*
        if (rc.hasFlag()) {
            moveToward(allySpawnZoneDirection());
        } else {
            moveInRandomDirection();
        }

         */
    }
}
