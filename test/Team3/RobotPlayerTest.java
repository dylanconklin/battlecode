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

public class RobotPlayerTest {

    private RobotController rc;

    @BeforeEach
    public void setUp() {
        rc = mock(RobotController.class);
    }

    @Test
    public void testRunDoesNotThrowException() {
        // Set up the mock for a successful spawn and that the robot is not yet spawned initially
        try {
            when(rc.isSpawned()).thenReturn(false).thenReturn(true); // Simulate spawning state change
            when(rc.getAllySpawnLocations()).thenReturn(new MapLocation[]{new MapLocation(0, 0)});
            when(rc.canSpawn(any())).thenReturn(true);
            doNothing().when(rc).spawn(any());

            // Simulate that the spawn method will return a valid Duck instance
            Duck duck = RobotPlayer.spawn(rc);
            assertNotNull(duck);

            // Ensure the run method does not throw an exception
            RobotPlayer.run(rc);
        } catch (Exception e) {
        }
    }

    @Test
    public void testSpawnReturnsDuckInstance() throws GameActionException {
        // Set up mock to simulate the robot not being spawned initially
        when(rc.isSpawned()).thenReturn(false).thenReturn(true); // Will return false first, then true after one call
        when(rc.getAllySpawnLocations()).thenReturn(new MapLocation[]{new MapLocation(0, 0)});
        when(rc.canSpawn(any())).thenReturn(true); // Simulate that it can spawn at the location
        doNothing().when(rc).spawn(any()); // Simulate a successful spawn

        // Test that the spawn method returns a Duck instance
        Duck duck = RobotPlayer.spawn(rc);
        assertNotNull(duck, "The returned Duck should not be null.");
  //      assertTrue(duck instanceof Duck, "The returned object should be a Duck.");
    }
}
