package Team3;

import battlecode.common.*;
import java.util.*;

public class AttackerDuck extends Duck {
    public AttackerDuck(RobotController rc) {
        super(rc);
        skill = SkillType.ATTACK;
    }

    @Override public void play() throws GameActionException {
        super.play();
    }

    public void attack() {}
    public void move() throws GameActionException {
        MapLocation[] locations = rc.getAllySpawnLocations();
        if (rc.hasFlag()) {
            // move toward ally spawn locations
            // TODO: don't move blindly toward locations[0]
            moveToward(locations[0]);
        } else {
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