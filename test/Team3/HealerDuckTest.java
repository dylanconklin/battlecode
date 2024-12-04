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

        assertEquals(1, foundCrumbs, "HealerDuck should find and move towards crumbs");
//        verify(rc, times(1)).move(currentLocation.directionTo(crumbLocation));
    }

    @Test
    void testAttack() throws GameActionException {
        RobotInfo enemy = mock(RobotInfo.class, withSettings().lenient());
        MapLocation enemyLocation = new MapLocation(7, 7);
        //when(enemy.getTeam()).thenReturn(rc.getTeam().opponent());
        when(enemy.getLocation()).thenReturn(enemyLocation);

        when(rc.senseNearbyRobots(GameConstants.VISION_RADIUS_SQUARED, rc.getTeam().opponent())).thenReturn(new RobotInfo[]{enemy});
        when(rc.canAttack(enemyLocation)).thenReturn(true);
        doNothing().when(rc).attack(enemyLocation);

        int attackedRobots = healerDuck.attack();

        assertEquals(1, attackedRobots, "HealerDuck should attack one enemy robot");
//        verify(rc, times(1)).attack(enemyLocation);
    }


}