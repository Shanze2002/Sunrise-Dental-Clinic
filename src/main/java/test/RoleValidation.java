package test;

public class RoleValidation {

    public boolean isValidRole(String roleName) {

        return roleName != null
                && !roleName.trim().isEmpty()
                && (roleName.equalsIgnoreCase("ADMIN")
                || roleName.equalsIgnoreCase("RECEPTIONIST")
                || roleName.equalsIgnoreCase("DOCTOR")
                || roleName.equalsIgnoreCase("CASHIER"));
    }
}