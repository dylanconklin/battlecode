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

class DuckTest {
    private Duck duck;
    private RobotController rc;

    @BeforeEach
    public void setUp() {
        rc = mock(RobotController.class);
        duck = new Duck(rc);
    }

    @Test
    void testUpdateEnemyRobots() throws GameActionException {
        Team myTeam = Team.A;
        Team opponentTeam = Team.B;

        // Mock team retrieval
        when(rc.getTeam()).thenReturn(myTeam);

        RobotInfo[] enemyRobots = {
                new RobotInfo(1, Team.A, 70, new MapLocation(1, 12), false, 1, 1, 1),
                new RobotInfo(2, Team.A, 100, new MapLocation(1, 123), false, 1, 1, 1)
        };
        when(rc.senseNearbyRobots(-1, Team.B)).thenReturn(enemyRobots);
        when(rc.canWriteSharedArray(0, enemyRobots.length)).thenReturn(true);
        when(rc.senseNearbyRobots(-1, Team.B)).thenReturn(enemyRobots);


        duck.updateEnemyRobots();
        verify(rc, times(1)).writeSharedArray(0, enemyRobots.length);
    }

}
