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

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
class AttackerDuckTest {
    private AttackerDuck attackerDuck;
    private RobotController rc;
    private Duck duck;
    @BeforeEach
    public void setUp() {
        rc = mock(RobotController.class);
        attackerDuck = new AttackerDuck(rc);
        duck = new Duck(rc, SkillType.ATTACK);
    }

    @Test
    public void testAttack() throws GameActionException {
        MapLocation[] locations = new MapLocation[3];
        RobotInfo[] allNearby = new RobotInfo[3];
        locations[0] = new MapLocation(1, 1);
        locations[1] = new MapLocation(2, 2);
        locations[2] = new MapLocation(3, 3);
        when(rc.senseNearbyRobots(-1)).thenReturn(allNearby);
        when(rc.senseNearbyCrumbs(-1)).thenReturn(locations);
        assertEquals(1, 1);
    }
    @Test
    public void testAttackFail() throws GameActionException {
        RobotInfo enemy = mock(RobotInfo.class);
        MapLocation enemyLocation = new MapLocation(7, 7);

        when(enemy.getTeam()).thenReturn(Team.B);
        when(enemy.getLocation()).thenReturn(enemyLocation);
        when(rc.senseNearbyRobots()).thenReturn(new RobotInfo[]{enemy});
        when(rc.canAttack(enemyLocation)).thenReturn(true);
        doNothing().when(rc).attack(enemyLocation);
        boolean didAttack = attackerDuck.attack();
        assertFalse(didAttack);

    }
    @Test
    public void testAllySpawnZoneDirectionWithValidLocations() throws GameActionException {
        MapLocation[] allySpawnLocationsArray = {
                new MapLocation(1, 1),
                new MapLocation(2, 2),
                new MapLocation(3, 3)
        };
        when(rc.getAllySpawnLocations()).thenReturn(allySpawnLocationsArray);

        MapLocation currentLocation = new MapLocation(0, 0);
        when(rc.getLocation()).thenReturn(currentLocation);

        Direction result = attackerDuck.allySpawnZoneDirection();
        assertNotNull(result);
        assertEquals(currentLocation.directionTo(allySpawnLocationsArray[0]), result);
    }

    @Test
    public void testAttackerDuckConstructor() {
        RobotController mockRC = mock(RobotController.class);
        AttackerDuck attackerDuck = new AttackerDuck(mockRC);
        assertNotNull(attackerDuck);
    }

}