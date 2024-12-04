package Team3;
import static Team3.BuilderDuck.FOUR;
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
    void testDefendFlagNoFlag() throws GameActionException {
        FlagInfo flag = new FlagInfo(new MapLocation(3, 3), Team.A, true,12);
        FlagInfo[] flags = { flag };
        when(rc.senseNearbyFlags(GameConstants.VISION_RADIUS_SQUARED, rc.getTeam()))
                .thenReturn(flags);

        assertFalse(builderDuck.defendFlag());
    }
    @Test
    void testPlaceTraps() throws GameActionException {
        when(rc.getLocation()).thenReturn(new MapLocation(1, 1));
        when(rc.canBuild(any(TrapType.class), any(MapLocation.class))).thenReturn(true);
        assertTrue(builderDuck.placeTraps());
    }
    @Test
    void testPlaceTrapsNoPlacement() throws GameActionException {
        when(rc.getLocation()).thenReturn(new MapLocation(1, 1));
        when(rc.canBuild(any(TrapType.class), any(MapLocation.class))).thenReturn(false);
        assertFalse(builderDuck.placeTraps());
    }

    @Test
    void testHandleWaterObstacles() throws GameActionException {
        MapLocation waterLocation = new MapLocation(1, 1);
        when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
        when(rc.canFill(waterLocation)).thenReturn(true);
        assertTrue(builderDuck.handleWaterObstacles());
    }
    @Test
    void testHandleWaterObstaclesNoFill() throws GameActionException {
        when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
        when(rc.canFill(any(MapLocation.class))).thenReturn(false);
        assertFalse(builderDuck.handleWaterObstacles());
    }
}