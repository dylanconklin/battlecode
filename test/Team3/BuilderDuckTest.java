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
//    @Test
//    void testCollectCrumbs() throws GameActionException {
//        MapLocation[] locations = new MapLocation[3];
//        locations[0] = new MapLocation(1, 1);
//        locations[1] = new MapLocation(2, 2);
//        locations[2] = new MapLocation(3, 3);
//        when(rc.senseNearbyCrumbs(-1)).thenReturn(locations);
//        builderDuck.collectCrumbs();
//    }
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
        // Mock trap placement
        when(rc.getLocation()).thenReturn(new MapLocation(1, 1));
        when(rc.canBuild(any(TrapType.class), any(MapLocation.class))).thenReturn(true);

        // Assume the trap can be placed at a given direction
        assertTrue(builderDuck.placeTraps());
    }
    @Test
    void testPlaceTrapsNoPlacement() throws GameActionException {
        // Test for when traps can't be placed
        when(rc.getLocation()).thenReturn(new MapLocation(1, 1));
        when(rc.canBuild(any(TrapType.class), any(MapLocation.class))).thenReturn(false);

        assertFalse(builderDuck.placeTraps());
    }

    @Test
    void testHandleWaterObstacles() throws GameActionException {
        // Mock water obstacle handling
        MapLocation waterLocation = new MapLocation(1, 1);
        when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
        when(rc.canFill(waterLocation)).thenReturn(true);

        // Test that it returns true when it fills water
        assertTrue(builderDuck.handleWaterObstacles());
    }
    @Test
    void testHandleWaterObstaclesNoFill() throws GameActionException {
        // Test for when water can't be filled
        when(rc.getLocation()).thenReturn(new MapLocation(0, 0));
        when(rc.canFill(any(MapLocation.class))).thenReturn(false);

        assertFalse(builderDuck.handleWaterObstacles());
    }

}