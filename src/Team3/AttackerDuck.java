package Team3;

import battlecode.common.*;

public class AttackerDuck extends Duck {
    public AttackerDuck(RobotController rc) {
        super(rc);
        skill = SkillType.ATTACK;
    }

    @Override
    public void play() throws GameActionException {
        attack();
        lookForFlag();
        move();
    }

    public void attack() throws GameActionException {
        RobotInfo[] robotInfos = rc.senseNearbyRobots();
        Team rcTeam = rc.getTeam();
        for (RobotInfo robot : robotInfos) {
             if (rc.canAttack(robot.location)) {
                rc.attack(robot.location);
             }
        }
    }

    public void move() throws GameActionException {
        if (rc.getRoundNum() >= GameConstants.SETUP_ROUNDS && (rc.hasFlag() || rc.getHealAmount() <= 300)) {
            // move toward ally spawn locations
            // TODO: don't move blindly toward locations[0]
            moveToward(allySpawnZoneDirection());
        } else if (rc.getRoundNum() >= GameConstants.SETUP_ROUNDS) {
            // move toward adversary spawn locations
            // TODO: don't move blindly away from locations[0]
            moveToward(enemySpawnZoneDirection());
        } else {
            boolean didMove = false;
            while (!didMove) {
                Direction otherDirection = randomDirection();
                didMove = moveToward(otherDirection);
            }
        }
    }
}