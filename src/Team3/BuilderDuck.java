package Team3;

import battlecode.common.*;

public class BuilderDuck extends Duck {
    public BuilderDuck(RobotController rc) {
        super(rc);
        skill = SkillType.HEAL;
    }

    @Override
    public void play() throws GameActionException {
    }
}
