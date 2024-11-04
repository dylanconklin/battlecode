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
import static org.junit.jupiter.api.Assertions.assertEquals;
class AttackerDuckTest {
    private AttackerDuck attackerDuck;
    private RobotController rc;

    @BeforeEach
    public void setUp() {
        rc = mock(RobotController.class);
        attackerDuck = new AttackerDuck(rc);
    }

    @Test
    public void test() {
        assertEquals(1, 1);
    }
    @Test
    public void test2() {
        assertEquals(1, 1);
    }

}
