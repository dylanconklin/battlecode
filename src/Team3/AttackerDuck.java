package Team3;

import battlecode.common.*;

import java.util.Arrays;
import java.util.Comparator;

public final class AttackerDuck extends Duck {
    public AttackerDuck(final RobotController rc) {
        super(rc);
        skill = SkillType.ATTACK;
    }

    @Override
    public void play() throws GameActionException {
        super.setupPlay();
        if (rc.canBuyGlobal(GlobalUpgrade.ATTACK)) {
            rc.buyGlobal(GlobalUpgrade.ATTACK);
        }
        attack();
        lookForFlag();
        move();
    }

    public int attack() throws GameActionException {
        RobotInfo[] robotInfos = rc.senseNearbyRobots();
        for (RobotInfo robot : robotInfos) {
            if (rc.canAttack(robot.location)) {
                rc.attack(robot.location);
            }
        }
        return robotInfos.length;
    }

    public int move() throws GameActionException {
        RobotInfo[] enemies = Arrays.stream(rc.senseNearbyRobots()).filter(robot -> robot.getTeam() != rc.getTeam()).sorted(Comparator.comparing(r -> r.getHealth())).toArray(RobotInfo[]::new);
        if (enemies.length > 0 && rc.getHealth() >= healthThreshold) {
            moveToward(enemies[0].location);
        } else if (rc.hasFlag() || rc.getHealAmount() < healthThreshold) {
            // move toward ally spawn locations
            moveToward(allySpawnZoneDirection());
        } else if (rc.getRoundNum() <= GameConstants.SETUP_ROUNDS) {
            moveInRandomDirection();
        } else {
            moveToward(enemySpawnZoneDirection());
        }
        return 1;
    }
}
