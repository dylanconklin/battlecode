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
        when(rc.getTeam()).thenReturn(Team.A); // Mock a valid team
        when(rc.getTeam().opponent()).thenReturn(Team.B); // Mock the opponent team
        healerDuck = new HealerDuck(rc);
    }
    @Test
    void testExploreAround() throws GameActionException {
        MapLocation currentLocation = new MapLocation(5, 5);
        MapLocation crumbLocation = new MapLocation(6, 6);
        MapLocation[] crumbs = { crumbLocation };

        when(rc.getLocation()).thenReturn(currentLocation);
        when(rc.senseNearbyCrumbs(-1)).thenReturn(crumbs);
        doNothing().when(rc).move(currentLocation.directionTo(crumbLocation));

        int foundCrumbs = healerDuck.exploreAround();

        assertEquals(1, foundCrumbs);

    }

    @Test
    void testAttack() throws GameActionException {
        RobotInfo enemy = mock(RobotInfo.class, withSettings().lenient());
        MapLocation enemyLocation = new MapLocation(7, 7);
        when(enemy.getLocation()).thenReturn(enemyLocation);
        when(rc.senseNearbyRobots(GameConstants.VISION_RADIUS_SQUARED, rc.getTeam().opponent())).thenReturn(new RobotInfo[]{enemy});
        when(rc.canAttack(enemyLocation)).thenReturn(true);
        doNothing().when(rc).attack(enemyLocation);
        int attackedRobots = healerDuck.attack();
        assertEquals(1, attackedRobots);

    }

    @Test
    void testAttackNoEnemies() throws GameActionException {
        when(rc.senseNearbyRobots(GameConstants.VISION_RADIUS_SQUARED, rc.getTeam().opponent())).thenReturn(new RobotInfo[]{});

        int attackedRobots = healerDuck.attack();

        assertEquals(0, attackedRobots);
        verify(rc, never()).attack(any(MapLocation.class));
    }
    @Test
    void testHealAlly() throws GameActionException {
        RobotInfo ally = mock(RobotInfo.class);
        MapLocation allyLocation = new MapLocation(8, 8);
        when(ally.getLocation()).thenReturn(allyLocation);
        when(ally.getHealth()).thenReturn(100);
        when(rc.senseNearbyRobots(GameConstants.VISION_RADIUS_SQUARED, rc.getTeam().opponent())).thenReturn(new RobotInfo[]{});
        when(rc.senseNearbyRobots(any(MapLocation.class), anyInt(), any(Team.class))).thenReturn(new RobotInfo[]{ally});
        when(rc.isActionReady()).thenReturn(true);
        when(rc.canHeal(allyLocation)).thenReturn(true);
        doNothing().when(rc).heal(allyLocation);

        boolean didHeal = healerDuck.heal_ally();

        assertFalse(didHeal);
    }


    @Test
    void testHealAlly3() throws GameActionException {

        RobotInfo ally = mock(RobotInfo.class);
        MapLocation allyLocation = new MapLocation(8, 8);
        when(ally.getLocation()).thenReturn(allyLocation);
        when(ally.getHealth()).thenReturn(100);

        when(rc.senseNearbyRobots(GameConstants.VISION_RADIUS_SQUARED, rc.getTeam().opponent())).thenReturn(new RobotInfo[]{});

        when(rc.senseNearbyRobots(any(MapLocation.class), anyInt(), any(Team.class))).thenReturn(new RobotInfo[]{ally});
        when(rc.isActionReady()).thenReturn(true);
        when(rc.canHeal(allyLocation)).thenReturn(true);
        doNothing().when(rc).heal(allyLocation);


        boolean didHeal = healerDuck.heal_ally();
        assertFalse(didHeal);

        when(rc.senseNearbyRobots(GameConstants.VISION_RADIUS_SQUARED, rc.getTeam().opponent())).thenReturn(new RobotInfo[]{});
        when(rc.isActionReady()).thenReturn(false); // Test when action is not ready
        didHeal = healerDuck.heal_ally();
        assertFalse(didHeal);


        when(rc.senseNearbyRobots(any(MapLocation.class), anyInt(), any(Team.class))).thenReturn(new RobotInfo[]{});
        didHeal = healerDuck.heal_ally();
        assertFalse(didHeal);


        when(rc.senseNearbyRobots(any(MapLocation.class), anyInt(), any(Team.class)))
                .thenReturn(new RobotInfo[]{mock(RobotInfo.class)});
        when(rc.canHeal(any(MapLocation.class))).thenReturn(false); // Cannot heal
        didHeal = healerDuck.heal_ally();
        assertFalse(didHeal);

        when(rc.canMove(any(Direction.class))).thenReturn(false);
        when(rc.adjacentLocation(any(Direction.class))).thenReturn(new MapLocation(0, 0));
        didHeal = healerDuck.heal_ally();
        assertFalse(didHeal);
    }

}