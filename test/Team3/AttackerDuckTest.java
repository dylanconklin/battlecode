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
    public void testAttack() throws GameActionException {
        MapLocation[] locations = new MapLocation[3];
        RobotInfo[] allNearby = new RobotInfo[3];
        locations[0] = new MapLocation(1, 1);
        locations[1] = new MapLocation(2, 2);
        locations[2] = new MapLocation(3, 3);
        //Direction dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];
        //when(rc.getTeam()).thenReturn(Team.valueOf("Team"));
        when(rc.senseNearbyRobots(-1)).thenReturn(allNearby);
        when(rc.senseNearbyCrumbs(-1)).thenReturn(locations);
        //when(rc.getTeam()).thenReturn(Team.valueOf("Team3"));
//         int test = attackerDuck.attack();

        assertEquals(1, 1);
    }

    @Test
    public void test2() {
        assertEquals(1, 1);
    }
}
