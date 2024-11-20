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
    private RobotController mockRc;
    private AttackerDuck attackerDuck;
    private RobotController rc;

    @BeforeEach
    public void setUp() {
        mockRc = mock(RobotController.class); // Mock RobotController
        attackerDuck = new AttackerDuck(mockRc); // Pass mocked RobotController to AttackerDuck

    }

    @Test
    public void testAttackNoEnemies() throws GameActionException {
        when(mockRc.senseNearbyRobots()).thenReturn(new RobotInfo[]{});
        int result = attackerDuck.attack();
        verify(mockRc, never()).attack(any(MapLocation.class));
        assertEquals(0, result, "Attack should return 0 when no enemies are nearby.");
    }
}
