// package com.andrew.db;

// import static org.junit.Assert.*;

// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;

// import org.junit.Before;
// import org.junit.Test;

// import com.andrew.model.Point;

// public class PointServiceTest {
//     private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
//     private PointService pointService;

//     @Before
//     public void setUp() {
//         pointService = new PointService();
//     }

//     @Test
//     public void testAddPoint() {
//         Point p = new Point(1.0, 2.0, 3, dtf.format(LocalDateTime.now()), 12, true);
//         pointService.insertPoint(p);
//     }

//     @Test
//     public void testAddNullPoint() {
//         assertThrows(RuntimeException.class, () -> {
//             pointService.insertPoint(null);
//         });
//     }
// }

package com.andrew.db;

import junit.framework.TestCase;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.andrew.model.Point;

public class PointServiceTest extends TestCase {
    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
    private PointService pointService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        pointService = new PointService();
    }

    public void testAddPoint() {
        Point p = new Point(1.0, 2.0, 3, dtf.format(LocalDateTime.now()), 12, true);
        pointService.insertPoint(p);
    }

    public void testAddNullPoint() {
        try {
            pointService.insertPoint(null);
            fail("Expected RuntimeException to be thrown");
        } catch (RuntimeException e) {
            // expected
        }
    }
}
