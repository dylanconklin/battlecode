package Team3;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import Team3.Duck;
import Team3.AttackerDuck;
import Team3.HealerDuck;
import battlecode.common.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class HealerDuckTest {
    private HealerDuck healerDuck;
    private RobotController rc;

    @BeforeEach
    void setUp() throws GameActionException {
        rc = mock(RobotController.class);
        healerDuck = new HealerDuck(rc);
    }


}