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

import java.lang.reflect.Type;
import java.util.ArrayList;


public class HealerDuckTest {
    private HealerDuck healerDuck;
    private RobotController rc;

    @BeforeEach
    public void setUp() throws GameActionException {
        rc = mock(RobotController.class);
        healerDuck = new HealerDuck(rc);
    }


    @Test
    void testHealerExploreAround() throws GameActionException {
        // Create an array of MapLocation with a size of 3
        MapLocation[] locations = new MapLocation[3];
        locations[0] = new MapLocation(1, 1);
        locations[1] = new MapLocation(2, 2);
        locations[2] = new MapLocation(3, 3);
        Direction dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];
        //when(rc.getTeam()).thenReturn(Team.valueOf("Team"));
        when(rc.senseNearbyCrumbs(-1)).thenReturn(locations);
        when(rc.getLocation()).thenReturn(locations[0]);
        when(healerDuck.moveToward(dir)).thenReturn(true);
        when(rc.canFill(locations[0])).thenReturn(true);
        int a = healerDuck.exploreAround();
        assertEquals(1, a);
    }
    @Test
    void testHeal() throws GameActionException {
        boolean a= healerDuck.heal();
        RobotController mockedRc = Mockito.mock(RobotController.class);
        assertFalse(a);
    }
}