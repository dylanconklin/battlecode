package Team3;

import battlecode.common.*;
public class HealerDuck extends Duck {

    //total health is 1000 and healing to be done when life drops below 900.
    private static final int MAX_HEALTH_THRESHOLD = 900;

    public HealerDuck(RobotController rc) {
        super(rc);
        skill = SkillType.HEAL;

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
        MapLocation ml = rc.getLocation();
        if (ml == null)
        {
            // need to debug the issue with move why the location is null sometime. 
            System.out.println("DBG: H: ml is null");
            return;
        }

        if (!heal_ally()) {  // Try to heal first, and only proceed if no healing was done
            lookForFlag();
            exploreAround();
            move();
        }

    }

    public boolean heal_ally() throws GameActionException {
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
                int b_heal_lvl = ally.getHealLevel();
                int b_health_lvl = ally.getHealth();
                if(ally.getHealLevel() >0 ||ally.getHealth()< 1000) {
                   // System.out.println("DBG: Heal stats" + ally.getHealLevel() + "health: " + ally.getHealth());
                }

                // healing should be enabled when health is less than 1000.
                if (ally.getHealth() < MAX_HEALTH_THRESHOLD) {

                    // Heal the ally if it's within healing range

                    if (rc.canHeal(ally.location)) {

                        rc.heal(ally.location);

                        // add experience while healing.
                        rc.getExperience(skill);

                        didHeal = true;
                        int a_heal_lvl = ally.getHealLevel();
                        int a_health_lvl = ally.getHealth();
                        if(a_health_lvl!= b_health_lvl) {
                            System.out.println("DBG: Heal stats after" + ally.getHealLevel() + "health: " + ally.getHealth());
                        }

                        //break;
                    }
                }

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
