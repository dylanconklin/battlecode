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

    public void attack() {}
    public void move() {}
}