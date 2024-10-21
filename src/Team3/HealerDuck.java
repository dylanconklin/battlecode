package Team3;

import battlecode.common.*;

public class HealerDuck extends Duck {
    public HealerDuck(RobotController rc) throws GameActionException {
        super(rc);
        skill = SkillType.HEAL;
    }

    public void lookForFlag() throws GameActionException {
        FlagInfo[] flags = rc.senseNearbyFlags(-1, rc.getTeam());
        for (FlagInfo flag : flags) {
            if (rc.canPickupFlag(flag.getLocation())) {
                rc.pickupFlag(flag.getLocation());
                break;
            }
        }
    }

    // this method will return true / false based on the fact if it is healing or not. this return can be utilized
    // to take a move action upon not healing.

    public void exploreAround() throws GameActionException {
        MapLocation[] closeByCrumbs = rc.senseNearbyCrumbs(-1);
        while (closeByCrumbs.length > 0) {
            closeByCrumbs = rc.senseNearbyCrumbs(-1);
            moveToward(closeByCrumbs[0]);
        }
    }

    @Override
    public void play() throws GameActionException {
        lookForFlag();
        exploreAround();
        move();
    }

    private boolean heal() throws GameActionException {
        // heal () should be called from move method.
        // sensing all the robots near in its vision to heal. it will heal only the ally robots.
        RobotInfo[] nearbyAllies = rc.senseNearbyRobots(-1, rc.getTeam());
        boolean didHeal = false;
        for (RobotInfo ally : nearbyAllies) {
            // need to find the constants and replace 100 with that HP constants (better not to use hardcode value)
            if (ally.getHealth() <= 300) {
                // Heal the ally if it's within healing range
                if (rc.canHeal(ally.location)) {
                    rc.heal(ally.location);
                    didHeal = true;  // Heal only one ally per turn
                }
            }
        }
        return didHeal;
    }

    public void move() throws GameActionException {
        if (rc.hasFlag()) {
            moveToward(allySpawnZoneDirection());
        } else {
            for (Direction otherDirection : Direction.allDirections()) {
                if (rc.canMove(otherDirection)) {
                    rc.move(otherDirection);
                    break;
                }
            }
        }
    }
}
