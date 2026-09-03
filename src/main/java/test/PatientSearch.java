package test;

public class PatientSearch {

    public boolean isValidSearch(String searchQuery) {

        return searchQuery != null
                && !searchQuery.trim().isEmpty();
    }
}