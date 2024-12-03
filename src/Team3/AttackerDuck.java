package Team3;

import battlecode.common.*;

import java.util.Arrays;
import java.util.Comparator;

public final class AttackerDuck extends Duck {
    /**
     * Constructor for AttackerDuck.
     *
     * @param rc
     */
    public AttackerDuck(final RobotController rc) {
        super(rc, SkillType.ATTACK);
    }

    @Override
    public boolean play() throws GameActionException {
        boolean playedSuccessfully = false;
        try {
            super.setupPlay();
            attack();
            lookForFlag();
            move();
            playedSuccessfully = true;
        } catch (GameActionException e) {
        }
        return playedSuccessfully;
    }

    /**
     * Look for nearby ducks to attack.
     *
     * @return Number of ducks attacked
     * @throws GameActionException
     */
    public int attack() throws GameActionException {
        RobotController rc = getRobotController();
        MapLocation[] enemyLocations = Arrays.stream(rc.senseNearbyRobots())
                .filter(robot -> rc.getTeam() != robot.getTeam())
                .filter(robot -> rc.canAttack(robot.location))
                .map(robot -> robot.location)
                .toArray(MapLocation[]::new);
        for (MapLocation location : enemyLocations) {
            try {
                rc.attack(location);
            } catch (GameActionException e) {
                break;
            }
        }
        return enemyLocations.length;
    }

    /**
     * Select movement strategy and move.
     *
     * @return True if Duck moved, False if it didn't
     * @throws GameActionException
     */
    public boolean move() throws GameActionException {
        RobotController rc = getRobotController();
        boolean didMove = false;
        RobotInfo[] enemies = Arrays.stream(rc.senseNearbyRobots())
                .filter(robot -> robot.getTeam() != rc.getTeam())
                .sorted(Comparator.comparing(r -> r.getHealth()))
                .toArray(RobotInfo[]::new);
        if (enemies.length > 0 && rc.getHealth() >= HEALTH_THRESHOLD) {
            didMove = moveToward(enemies[0].location);
        } else if (rc.hasFlag() || rc.getHealAmount() < HEALTH_THRESHOLD) {
            // move toward ally spawn locations
            didMove = moveToward(allySpawnZoneDirection());
        } else if (rc.getRoundNum() <= GameConstants.SETUP_ROUNDS) {
            didMove = moveInRandomDirection();
        } else {
            didMove = moveToward(enemySpawnZoneDirection());
        }
        return didMove;
    }
}
