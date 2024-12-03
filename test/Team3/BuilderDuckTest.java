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
}