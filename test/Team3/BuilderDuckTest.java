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

class BuilderDuckTest {
    private enum State { SETUP, DEFENDING, EXPLORING }
    private BuilderDuck builderDuck;
    private RobotController rc;

    @BeforeEach
    public void setUp() throws GameActionException {
        rc = mock(RobotController.class);
        builderDuck = new BuilderDuck(rc);
    }
    @Test
    void testCollectCrumbs() throws GameActionException {
        MapLocation[] locations = new MapLocation[3];
        locations[0] = new MapLocation(1, 1);
        locations[1] = new MapLocation(2, 2);
        locations[2] = new MapLocation(3, 3);
        when(rc.senseNearbyCrumbs(-1)).thenReturn(locations);
        builderDuck.collectCrumbs();
    }
    @Test
    void testGatherResources() throws GameActionException {
        MapLocation[] locations = new MapLocation[3];
        locations[0] = new MapLocation(1, 1);
        locations[1] = new MapLocation(2, 2);
        locations[2] = new MapLocation(3, 3);
        Direction dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];
        when(rc.senseNearbyCrumbs(-1)).thenReturn(locations);
        when(rc.getLocation()).thenReturn(locations[0]);
        when(builderDuck.moveToward(dir)).thenReturn(true);
        when(rc.canFill(locations[0])).thenReturn(true);
        int result = builderDuck.gatherResources();
        assertEquals(1, result);
    }
    @Test
    public void testPlaceTrap() throws GameActionException {
        MapLocation trapLocation = new MapLocation(0, 1);
        when(rc.canBuild(TrapType.EXPLOSIVE, trapLocation)).thenReturn(true);
        when(rc.getCrumbs()).thenReturn(100);
        builderDuck.placeTrap(TrapType.EXPLOSIVE, trapLocation);
        verify(rc, times(1)).build(TrapType.EXPLOSIVE, trapLocation);
    } @Test
    public void testPlaceTrap1() throws GameActionException {
        MapLocation trapLocation = new MapLocation(0, 1);
        when(rc.canBuild(TrapType.EXPLOSIVE, trapLocation)).thenReturn(true);
        when(rc.getCrumbs()).thenReturn(100);
        builderDuck.placeTrap(TrapType.EXPLOSIVE, trapLocation);
        verify(rc, times(1)).build(TrapType.EXPLOSIVE, trapLocation);
    }
    @Test
    public void testGuardFlag() throws GameActionException {
        when(rc.getTeam()).thenReturn(Team.A);
        when(rc.senseNearbyRobots(BuilderDuck.SENSING_RADIUS, rc.getTeam().opponent())).thenReturn(new RobotInfo[]{}); // No enemies detected
        builderDuck.guardFlag();
        assertEquals(BuilderDuck.State.SETUP, builderDuck.state);
        RobotInfo enemy1 = mock(RobotInfo.class);
        when(enemy1.getTeam()).thenReturn(Team.B);
        RobotInfo[] enemies = new RobotInfo[]{enemy1};
        when(rc.senseNearbyRobots(BuilderDuck.SENSING_RADIUS, rc.getTeam().opponent())).thenReturn(enemies); // 1 enemy detected
        builderDuck.guardFlag();
        assertEquals(BuilderDuck.State.SETUP, builderDuck.state);
        RobotInfo enemy2 = mock(RobotInfo.class);
        RobotInfo enemy3 = mock(RobotInfo.class);
        RobotInfo enemy4 = mock(RobotInfo.class);
        RobotInfo[] multipleEnemies = new RobotInfo[]{enemy1, enemy2, enemy3, enemy4};
        when(rc.senseNearbyRobots(BuilderDuck.SENSING_RADIUS, rc.getTeam().opponent())).thenReturn(multipleEnemies); // 4 enemies detected
        builderDuck.guardFlag();
        assertEquals(BuilderDuck.State.EXPLORING, builderDuck.state);
    }


}