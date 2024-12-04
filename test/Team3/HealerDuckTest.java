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
    private Duck duck;
    private RobotInfo mockTarget;
    @BeforeEach
    public void setUp() throws GameActionException {
        rc = mock(RobotController.class);
        when(rc.getTeam()).thenReturn(Team.A); // Mock a valid team
        when(rc.getTeam().opponent()).thenReturn(Team.B); // Mock the opponent team
        duck = new Duck(rc, SkillType.ATTACK);
        healerDuck = new HealerDuck(rc);
        mockTarget = mock(RobotInfo.class);
        when(mockTarget.getLocation()).thenReturn(new MapLocation(0, 0));
    }

    @Test
    void testAttack() throws GameActionException {
        RobotInfo enemy = mock(RobotInfo.class, withSettings().lenient());
        MapLocation enemyLocation = new MapLocation(7, 7);
        when(enemy.getLocation()).thenReturn(enemyLocation);
        when(rc.senseNearbyRobots(GameConstants.VISION_RADIUS_SQUARED, rc.getTeam().opponent())).thenReturn(new RobotInfo[]{enemy});
        when(rc.canAttack(enemyLocation)).thenReturn(true);
        doNothing().when(rc).attack(enemyLocation);
        boolean attackedRobots = healerDuck.attack();
        assertEquals(false, attackedRobots);

    }

@Test
public void testHealAllyNoTarget() throws GameActionException {
    when(healerDuck.healTarget()).thenReturn(null);
    when(rc.senseNearbyRobots()).thenReturn(new RobotInfo[0]);
    assertFalse(healerDuck.healAlly());
    verify(rc, never()).heal(any(MapLocation.class));
}

    @Test
    public void testHealAllyWithOpponentNearby() throws GameActionException {
        RobotInfo[] opponentArray = new RobotInfo[1];
        when(rc.senseNearbyRobots(GameConstants.VISION_RADIUS_SQUARED, rc.getTeam().opponent()))
                .thenReturn(opponentArray);
        boolean result = healerDuck.healAlly();
        assertFalse(result);
    }
    @Test
    public void testAllySpawnZoneDirectionWithSingleLocation() throws GameActionException {
        MapLocation[] allySpawnLocationsArray = {
                new MapLocation(1, 1)
        };
        when(rc.getAllySpawnLocations()).thenReturn(allySpawnLocationsArray);
        MapLocation currentLocation = new MapLocation(0, 0);
        when(rc.getLocation()).thenReturn(currentLocation);


        Direction result = healerDuck.allySpawnZoneDirection();
        assertNotNull(result);
        assertEquals(currentLocation.directionTo(allySpawnLocationsArray[0]), result);
    }

}