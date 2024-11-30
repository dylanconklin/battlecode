package Team3;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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
        for (RobotInfo robot : robotInfos) {
            if (rc.canAttack(robot.location)) {
                rc.attack(robot.location);
            }
        }
        return robotInfos.length;
    }

    public int move() throws GameActionException {
        RobotInfo[] enemies = Arrays.stream(rc.senseNearbyRobots()).filter(robot -> robot.getTeam() != rc.getTeam()).sorted(Comparator.comparing(r -> r.getHealth())).toArray(RobotInfo[]::new);
        if (enemies.length > 0 && rc.getHealth() >= 300) {
            moveToward(enemies[0].location);
        } else if (rc.hasFlag() || rc.getHealAmount() < 300) {
            // move toward ally spawn locations
            // TODO: don't move blindly toward locations[0]
            moveToward(allySpawnZoneDirection());
        } else if (rc.getRoundNum() <= GameConstants.SETUP_ROUNDS) {
            moveInRandomDirection();
        } else {
            moveToward(enemySpawnZoneDirection());
        }
        return 1;
    }
}