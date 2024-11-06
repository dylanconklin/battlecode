package Team3;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import battlecode.common.RobotController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RobotPlayerTest {

    private RobotController rc;

    @BeforeEach
    public void setUp() {
        rc = mock(RobotController.class);
    }
    @Test
    public void testSanity() {
        assertEquals(2, 1+1);
    }

}
