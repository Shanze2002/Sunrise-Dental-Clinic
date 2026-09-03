package test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class TreatmentValidationTest {

    @Test
    public void testValidTreatmentCost() {

        TreatmentValidation treatment = new TreatmentValidation();

        boolean result = treatment.isValidTreatmentCost(0);

        assertTrue(result);
    }

    @Test
    public void testInvalidTreatmentCost() {

        TreatmentValidation treatment = new TreatmentValidation();

        boolean result = treatment.isValidTreatmentCost(0);

        assertFalse(result);
    }
}