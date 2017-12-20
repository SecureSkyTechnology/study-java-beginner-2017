package testgroup;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.io.IOException;

public class AppTest {
    @Test
    public void testGetYourName() throws IOException {
        assertEquals("bob", App.getYourName("config/testconfig.properties"));
    }
}
