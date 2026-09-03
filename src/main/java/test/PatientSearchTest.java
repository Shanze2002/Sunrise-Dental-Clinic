package test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class PatientSearchTest {

    @Test
    public void testValidPatientSearch() {

        PatientSearch search = new PatientSearch();

        boolean result = search.isValidSearch("Kamal");

        assertTrue(result);
    }

    @Test
    public void testEmptyPatientSearch() {

        PatientSearch search = new PatientSearch();

        boolean result = search.isValidSearch("");

        assertFalse(result);
    }

    @Test
    public void testNullPatientSearch() {

        PatientSearch search = new PatientSearch();

        boolean result = search.isValidSearch(null);

        assertFalse(result);
    }
}