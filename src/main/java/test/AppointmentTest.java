package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AppointmentTest {

    @Test
    public void testGetEstimatedTotal() {

        // Create Appointment object
        Appointment appointment = new Appointment();

        // Set consultation fee
        appointment.setDoctorConsultationFee(2000.00);

        // Set treatment cost
        appointment.setTreatmentCost(3500.00);

        // Call the method being tested
        double result = appointment.getEstimatedTotal();

        // Expected result = 2000 + 3500 = 5500
        assertEquals(5500.00, result, 0.01);
    }
}

//Calculation Test and Data Get/Set Test:
