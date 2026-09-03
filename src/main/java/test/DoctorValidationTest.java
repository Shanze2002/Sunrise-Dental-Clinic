package test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class DoctorValidationTest {

    @Test
    public void testValidDoctorConsultationFee() {

        DoctorValidation doctor = new DoctorValidation();

        boolean result = doctor.isValidDoctorFee(2500.00);

        assertTrue(result);
    }

    @Test
    public void testInvalidDoctorConsultationFee() {

        DoctorValidation doctor = new DoctorValidation();

        boolean result = doctor.isValidDoctorFee(0);

        assertFalse(result);
    }
}

//doctor consultation fee එක validate කිරීම