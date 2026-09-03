package test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BillCalTest {

    @Test
    public void testGetSubtotal() {

        BillCal bill = new BillCal();

        double result = bill.getSubtotal(
                2000.00,
                3500.00,
                500.00
        );

        assertEquals(6000.00, result, 0.01);
    }
}