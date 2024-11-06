package Team3;

import battlecode.common.*;

public class AttackerDuck extends Duck {
    public AttackerDuck(RobotController rc) {
        super(rc);
        System.out.println("DBG: AttackDuck");

        skill = SkillType.ATTACK;
    }

    @Override
    public void play() throws GameActionException {
        super.setupPlay();
        attack();
        lookForFlag();
        move();

    }

    public int attack() throws GameActionException {
        RobotInfo[] robotInfos = rc.senseNearbyRobots();
        if (robotInfos != null) {
            //  Team rcTeam = rc.getTeam();
            for (RobotInfo robot : robotInfos) {
                if (rc.canAttack(robot.location)) {
                    rc.attack(robot.location);
                }
            }
            return robotInfos.length;
        } else
            return 0;
    }

    public int move() throws GameActionException {
        if (rc.getRoundNum() >= GameConstants.SETUP_ROUNDS && (rc.hasFlag() || rc.getHealAmount() <= 300)) {
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
        return 1;
    }
}