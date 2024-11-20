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
//    @Test
//    public void testMove() throws GameActionException {
//        when(mockRc.getTeam()).thenReturn(Team.A);
//
//        RobotInfo[] robots = {
//                new RobotInfo(1, Team.A, 70, new MapLocation(1, 12), false, 1, 1, 1),
//                new RobotInfo(2, Team.B, 100, new MapLocation(1, 123), false, 1, 1, 1)
//        };
//        when(mockRc.senseNearbyRobots()).thenReturn(new RobotInfo[]{robots[0]});
//        when(mockRc.getHealth()).thenReturn((int) 300.0);
//        doNothing().when(attackerDuck).moveToward(any(MapLocation.class));
//        attackerDuck.move();
//
//        // Verify movement toward the enemy
//        verify(mockRc, times(1)).move(any(Direction.class));
//    }
}
