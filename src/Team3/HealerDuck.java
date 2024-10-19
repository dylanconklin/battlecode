package Team3;

import battlecode.common.*;

public class HealerDuck extends Duck {
    public HealerDuck(RobotController rc) {
        super(rc);
        skill = SkillType.HEAL;
    }
}
