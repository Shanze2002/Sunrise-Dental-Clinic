package test;

import java.sql.Date;
import java.sql.Time;


public class AppointmentReg {

    public boolean isValidAppointment(
            String appointmentNumber,
            int patientId,
            int doctorId,
            int treatmentId,
            Date appointmentDate,
            Time appointmentTime) {

        return appointmentNumber != null
                && !appointmentNumber.trim().isEmpty()
                && patientId > 0
                && doctorId > 0
                && treatmentId > 0
                && appointmentDate != null
                && appointmentTime != null;
    }
}

//Book Appoinmnet
