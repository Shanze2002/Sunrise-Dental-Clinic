package test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RoleValidationTest {

    @Test
    public void testValidAdminRole() {

        RoleValidation role = new RoleValidation();

        boolean result = role.isValidRole("ADMIN");

        assertTrue(result);
    }
}