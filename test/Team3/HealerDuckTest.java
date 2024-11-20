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
    void testHealNoAction() throws GameActionException {
        when(rc.isActionReady()).thenReturn(false);
        when(rc.senseNearbyRobots(GameConstants.VISION_RADIUS_SQUARED, Team.B)).thenReturn(new RobotInfo[0]);
        boolean didHeal = healerDuck.heal_ally();
        assertFalse(didHeal);
        verify(rc, never()).heal(any(MapLocation.class));
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
    void testHealAllyNoHealing() throws GameActionException {
        RobotInfo[] robots = new RobotInfo[2];
        robots[0] = mock(RobotInfo.class);
        robots[1] = mock(RobotInfo.class);
        when(rc.senseNearbyRobots(GameConstants.VISION_RADIUS_SQUARED, Team.A)).thenReturn(robots);
        when(robots[0].getHealth()).thenReturn(100);
        when(robots[1].getHealth()).thenReturn(500);
        when(rc.canHeal(any(MapLocation.class))).thenReturn(false);
        doNothing().when(rc).heal(any(MapLocation.class));
        boolean didHeal = healerDuck.heal_ally();
        assertFalse(didHeal);
        verify(rc, never()).heal(any(MapLocation.class));
    }
}