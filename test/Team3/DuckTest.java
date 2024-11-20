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
        when(rc.getTeam()).thenReturn(myTeam);
        RobotInfo[] enemyRobots = {
                new RobotInfo(1, Team.A, 70, new MapLocation(1, 12), false, 1, 1, 1),
                new RobotInfo(2, Team.B, 100, new MapLocation(1, 123), false, 1, 1, 1)
        };
        when(rc.senseNearbyRobots(-1, Team.B)).thenReturn(enemyRobots);
        when(rc.canWriteSharedArray(0, enemyRobots.length)).thenReturn(true);
        when(rc.senseNearbyRobots(-1, Team.B)).thenReturn(enemyRobots);
        duck.updateEnemyRobots();
        verify(rc, times(1)).writeSharedArray(0, enemyRobots.length);
    }

    @Test
    void testPlaceTrap() throws GameActionException {
        MapLocation location = new MapLocation(4, 4);
        when(rc.canBuild(TrapType.EXPLOSIVE, location)).thenReturn(true);
        duck.placeTrap(TrapType.EXPLOSIVE, location);
        verify(rc, times(1)).build(TrapType.EXPLOSIVE, location);
        assertTrue(duck.hasCooldown());
    }
    @Test
    void testLookForFlag() throws GameActionException {
        FlagInfo flag = new FlagInfo(new MapLocation(3, 3), Team.A, true,12);
        FlagInfo[] flags = { flag };
        when(rc.senseNearbyFlags(-1, rc.getTeam())).thenReturn(flags);
        when(rc.canPickupFlag(flag.getLocation())).thenReturn(true);
        boolean pickedUpFlag = duck.lookForFlag();
        assertTrue(pickedUpFlag);
        verify(rc, times(1)).pickupFlag(flag.getLocation());
    }

    @Test
    void testHasCooldown() {
        assertFalse(duck.hasCooldown());
        duck.applyCooldown(5);
        assertTrue(duck.hasCooldown());
    }

    @Test
    void testReduceCooldown() {
        duck.applyCooldown(3);
        duck.reduceCooldown();
        assertTrue(duck.hasCooldown());
        duck.reduceCooldown();
        duck.reduceCooldown();
        assertFalse(duck.hasCooldown());
    }
    @Test
    void testMoveAwayFromLocation() throws GameActionException {
        MapLocation enemyLocation = new MapLocation(10, 10);
        Direction direction = Direction.SOUTH;
        when(rc.getLocation()).thenReturn(new MapLocation(5, 5));
        when(rc.canMove(direction)).thenReturn(true);

        boolean result = duck.moveAwayFrom(enemyLocation);

        assertFalse(result);
         //verify(rc, times(1)).move(direction);
    }

}
