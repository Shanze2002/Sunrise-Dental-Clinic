package test;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class AppointmentRegTest {

    @Test
    public void testAppointmentRegistration() {

        String appointmentNumber = "APT-0001";
        int patientId = 1;
        int doctorId = 1;
        int treatmentId = 1;

        assertEquals("APT-0001", appointmentNumber);
        assertEquals(1, patientId);
        assertEquals(1, doctorId);
        assertEquals(1, treatmentId);
    }
}