package test;

public class Paiten {

    public boolean isValidPatient(String fullName, String phone,
                                  String email, String address) {

        return fullName != null
                && !fullName.trim().isEmpty()
                && phone != null
                && !phone.trim().isEmpty()
                && email != null
                && !email.trim().isEmpty()
                && address != null
                && !address.trim().isEmpty();
    }
}