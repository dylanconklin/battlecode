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
        when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
    }
    @Test
    void testHealerExploreAround() throws GameActionException {
        MapLocation[] locations = {
                new MapLocation(1, 1),
                new MapLocation(2, 2),
                new MapLocation(3, 3)
        };
        when(rc.senseNearbyCrumbs(GameConstants.VISION_RADIUS_SQUARED)).thenReturn(locations, new MapLocation[0]);
        when(rc.canMove(any(Direction.class))).thenReturn(true);
        doNothing().when(rc).move(any(Direction.class));
        int foundCrumbs = healerDuck.exploreAround();
        assertEquals(1, foundCrumbs);
        verify(rc, times(1)).move(any(Direction.class));
    }

    @Test
    void testHealerMoveToward() throws GameActionException {
        MapLocation target = new MapLocation(5, 5);
        when(rc.canMove(any(Direction.class))).thenReturn(true);
        doNothing().when(rc).move(any(Direction.class));
        healerDuck.moveToward(target);
        verify(rc, atLeastOnce()).move(any(Direction.class));
    }
    @Test
    void testHealNoAction() throws GameActionException {
        when(rc.isActionReady()).thenReturn(false);
        when(rc.senseNearbyRobots(GameConstants.VISION_RADIUS_SQUARED, Team.B)).thenReturn(new RobotInfo[0]);
        boolean didHeal = healerDuck.heal_ally();
        assertFalse(didHeal);
        verify(rc, never()).heal(any(MapLocation.class));
    }
    @Test
    void testHealTarget() throws GameActionException {
        // Setup mock robots with varying priorities and health
        MapLocation center = new MapLocation(0, 0);

        RobotInfo[] robots = {
                new RobotInfo(1, Team.A, 70, new MapLocation(1, 12), false, 1, 1, 1),
                new RobotInfo(2, Team.B, 100, new MapLocation(1, 123), false, 1, 1, 1)
        };

        when(rc.senseNearbyRobots(center, GameConstants.VISION_RADIUS_SQUARED, Team.A)).thenReturn(robots);

        // Call the healTarget method
        RobotInfo target = healerDuck.healTarget(center, GameConstants.VISION_RADIUS_SQUARED);

        // Verify that the correct target was selected (Robot 1 should be chosen due to flag priority)
        assertNotNull(target);
        assertEquals(robots[0], target);  // Robot 1 should be the target because it has the lowest health

        // Check the behavior when no robots are within range
        when(rc.senseNearbyRobots(center, GameConstants.VISION_RADIUS_SQUARED, Team.A)).thenReturn(new RobotInfo[0]);
        target = healerDuck.healTarget(center, GameConstants.VISION_RADIUS_SQUARED);
        assertNull(target);  // No robots in range, so target should be null
    }
}