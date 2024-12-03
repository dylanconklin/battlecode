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
        duck = new Duck(rc, SkillType.ATTACK);
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

        when(rc.senseNearbyRobots(-1, opponentTeam)).thenReturn(enemyRobots);
        when(rc.canWriteSharedArray(anyInt(), anyInt())).thenReturn(true);

        duck.updateEnemyRobots();

        verify(rc, times(1)).writeSharedArray(anyInt(), anyInt());
    }

    @Test
    void testLookForFlag() throws GameActionException {
        FlagInfo flag = new FlagInfo(new MapLocation(3, 3), Team.A, true, 12);
        FlagInfo[] flags = { flag };

        when(rc.senseNearbyFlags(-1, rc.getTeam())).thenReturn(flags);
        when(rc.canPickupFlag(flag.getLocation())).thenReturn(true);

        boolean pickedUpFlag = duck.lookForFlag();

        assertTrue(pickedUpFlag);
        verify(rc, times(1)).pickupFlag(flag.getLocation());
    }

    @Test
    void testMoveAwayFromLocation() throws GameActionException {
        MapLocation enemyLocation = new MapLocation(10, 10);
        when(rc.getLocation()).thenReturn(new MapLocation(5, 5));
        when(rc.canMove(any(Direction.class))).thenReturn(true);

        boolean result = duck.moveAwayFrom(enemyLocation);

        assertTrue(result);
        verify(rc, atLeastOnce()).move(any(Direction.class));
    }

    @Test
    void testMoveToward() throws GameActionException {
        MapLocation currentLocation = new MapLocation(0, 0);
        Direction direction = Direction.NORTH;
        MapLocation targetLocation = currentLocation.add(direction);

        when(rc.getLocation()).thenReturn(currentLocation);
        when(rc.canMove(direction)).thenReturn(true);

        boolean moved = duck.moveToward(direction);

        assertTrue(moved);
        verify(rc, times(1)).move(direction);
    }

    @Test
    void testMoveInRandomDirection() throws GameActionException {
        when(rc.canMove(any(Direction.class))).thenReturn(true);

        boolean moved = duck.moveInRandomDirection();

        assertTrue(moved);
        verify(rc, atLeastOnce()).move(any(Direction.class));
    }

}
