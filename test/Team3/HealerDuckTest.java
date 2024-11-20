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
        when(rc.getTeam()).thenReturn(Team.A); // Set the team to A
        when(rc.getLocation()).thenReturn(new MapLocation(0, 0)); // Default location
    }


    @Test
    void testHealerExploreAround() throws GameActionException {
        // Setup: Mock nearby crumbs and movement
        MapLocation[] locations = {
                new MapLocation(1, 1),
                new MapLocation(2, 2),
                new MapLocation(3, 3)
        };
        when(rc.senseNearbyCrumbs(GameConstants.VISION_RADIUS_SQUARED)).thenReturn(locations, new MapLocation[0]);
        when(rc.canMove(any(Direction.class))).thenReturn(true);
        doNothing().when(rc).move(any(Direction.class));

        // Action: Call exploreAround()
        int foundCrumbs = healerDuck.exploreAround();

        // Verify: Check the results and interactions
        assertEquals(1, foundCrumbs);
        verify(rc, times(1)).move(any(Direction.class));
    }
    @Test
    void testHealNoAction() throws GameActionException {
        // Setup: Mock isActionReady() and no enemy robots nearby
        when(rc.isActionReady()).thenReturn(false);
        when(rc.senseNearbyRobots(GameConstants.VISION_RADIUS_SQUARED, Team.B)).thenReturn(new RobotInfo[0]);

        // Action: Call heal_ally()
        boolean didHeal = healerDuck.heal_ally();

        // Verify: Healing should not happen as action is not ready
        assertFalse(didHeal);
        verify(rc, never()).heal(any(MapLocation.class));
    }
    @Test
    void testHealerMoveToward() throws GameActionException {
        // Setup: Mock movement capabilities
        MapLocation target = new MapLocation(5, 5);
        when(rc.canMove(any(Direction.class))).thenReturn(true);
        doNothing().when(rc).move(any(Direction.class));

        // Action: Call moveToward (assumed implementation in Duck)
        healerDuck.moveToward(target);

        // Verify: Ensure movement logic was executed
        verify(rc, atLeastOnce()).move(any(Direction.class));
    }
}