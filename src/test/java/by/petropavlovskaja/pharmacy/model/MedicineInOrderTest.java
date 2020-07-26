package by.petropavlovskaja.pharmacy.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MedicineInOrderTest {

    MedicineInOrder test1 = new MedicineInOrder(78, "Лордес", 10, "5 мг", false, 5, 789, 8);
    MedicineInOrder test2 = new MedicineInOrder(79, "Синупрет", 10, "-", true, 2, 1002, 8);
    MedicineInOrder test3 = new MedicineInOrder(104, "АмброГексал", 1, "7,5 мг/мл", true, 1, 544, 12);
    MedicineInOrder test4 = new MedicineInOrder(1, "Test", 50, "7,5 мг/мл", true, 11, 544, 12);
    MedicineInOrder test5 = new MedicineInOrder(1, "Test", 50, "7,5 мг/мл", true, 11, 544, 12);

    @Test
    void setRubCoin() {
        test3.setRubCoin();
        assertEquals(544, test3.getPriceForQuantity());
        assertEquals(5, test3.getRubForOne());
        assertEquals(44, test3.getCoinForOne());
        assertEquals(5, test3.getRubForQuantity());
        assertEquals(44, test3.getCoinForOne());
    }

    @Test
    void setPriceForOne() {
        test3.setPriceForOne(789);
        assertEquals(789, test3.getPriceForOne());
    }

    @Test
    void isRecipeRequired() {
        assertFalse(test1.isRecipeRequired());
        assertTrue(test2.isRecipeRequired());
    }

    @Test
    void getId() {
        assertEquals(78, test1.getId());
        assertEquals(79, test2.getId());
    }

    @Test
    void getMedicine() {
        assertEquals("Лордес", test1.getMedicine());
        assertEquals("Синупрет", test2.getMedicine());
    }
    @Test

    void getDosage() {
        assertEquals("5 мг", test1.getDosage());
        assertEquals("-", test2.getDosage());
    }

    @Test
    void getQuantity() {
        assertEquals(5, test1.getQuantity());
        assertEquals(2, test2.getQuantity());
    }

    @Test
    void getPriceForOne() {
        assertEquals(789, test1.getPriceForOne());
        assertEquals(1002, test2.getPriceForOne());
    }

    @Test
    void getIndivisibleAmount() {
        assertEquals(10, test1.getIndivisibleAmount());
        assertEquals(10, test2.getIndivisibleAmount());
    }

    @Test
    void getFkOrder() {
        assertEquals(8, test1.getFkOrder());
        assertEquals(8, test2.getFkOrder());
    }

    @Test
    void getAmount() {
        test3.setAmount(84);
        assertEquals(84, test3.getAmount());
    }

    @Test
    void getRubForOne() {
        test1.setRubCoin();
        test2.setRubCoin();
        assertEquals(7, test1.getRubForOne());
        assertEquals(10, test2.getRubForOne());
    }

    @Test
    void getCoinForOne() {
        test1.setRubCoin();
        test2.setRubCoin();
        assertEquals(89, test1.getCoinForOne());
        assertEquals(2, test2.getCoinForOne());
    }

    @Test
    void getPriceForQuantity() {
        test1.setRubCoin();
        test2.setRubCoin();
        assertEquals(3945, test1.getPriceForQuantity());
        assertEquals(2004, test2.getPriceForQuantity());
    }

    @Test
    void getRubForQuantity() {
        test1.setRubCoin();
        test2.setRubCoin();
        assertEquals(39, test1.getRubForQuantity());
        assertEquals(20, test2.getRubForQuantity());
    }

    @Test
    void getCoinForQuantity() {
        test1.setRubCoin();
        test2.setRubCoin();
        assertEquals(45, test1.getCoinForQuantity());
        assertEquals(4, test2.getCoinForQuantity());
    }

    @Test
    void setQuantity() {
        test3.setQuantity(24);
        assertEquals(24, test3.getQuantity());
    }

    @Test
    void setAmount() {
        test3.setAmount(845);
        assertEquals(845, test3.getAmount());
    }

    @Test
    void testEquals() {
        assertTrue(test4.equals(test5));
    }

    @Test
    void testHashCode() {
        assertEquals(test5, test5);
    }
}