package test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AuthTest {

    @Test
    public void testValidLoginInput() {

        Auth auth = new Auth();

        boolean result = auth.isValidLoginInput(
                "Admin",
                ""
        );

        assertTrue(result);
    }
}