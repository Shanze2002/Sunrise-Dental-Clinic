package test;

public class BillCal {

    public double getSubtotal(
            double consultationFee,
            double treatmentCost,
            double additionalCharges) {

        return consultationFee
                + treatmentCost
                + additionalCharges;
    }
}

//bill calculatin method eka