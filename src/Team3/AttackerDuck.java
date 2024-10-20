package Team3;

import battlecode.common.*;

public class AttackerDuck extends Duck {
    public AttackerDuck(RobotController rc) {
        super(rc);
        skill = SkillType.ATTACK;
    }

    @Override public void play() throws GameActionException {
        super.play();
    }

    public void attack() throws GameActionException {
        RobotInfo[] robotInfos = rc.senseNearbyRobots();
        Team rcTeam = rc.getTeam();
        for (RobotInfo robot : robotInfos) {
            if (robot.team != rcTeam && rc.canAttack(robot.location)) {
                rc.attack(robot.location);
            }
        }
    }

    public void move() throws GameActionException {
        MapLocation[] locations = rc.getAllySpawnLocations();
        while (rc.getRoundNum() >= GameConstants.SETUP_ROUNDS && ( rc.hasFlag() || rc.getHealAmount() <= 300 )) {
            // move toward ally spawn locations
            // TODO: don't move blindly toward locations[0]
            moveTowardAllySpawnZone();
        }
        if (!rc.hasFlag() && rc.getRoundNum() >= GameConstants.SETUP_ROUNDS) {
            // move toward adversary spawn locations
            // TODO: don't move blindly away from locations[0]
            moveAwayFrom(locations[0]);
        }
    }

    @Override public void moveToward(MapLocation location) throws GameActionException {
        Direction direction = rc.getLocation().directionTo(location);
        if (rc.canMove(direction)) {
            rc.move(direction);
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