package test;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class PaitenTest {

    @Test
    public void testValidPatientRegistration() {

        Paiten patient = new Paiten();

        boolean result = patient.isValidPatient(
                "John Silva",
                "0712345678",
                "john@gmail.com",
                "Colombo"
        );

        assertTrue(result);
    }
}