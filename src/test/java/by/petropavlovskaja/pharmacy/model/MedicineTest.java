package by.petropavlovskaja.pharmacy.model;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class MedicineTest {
    Medicine test1 = new Medicine(1, "Парацетамол", 1, 39, "30 мг/мл", getDate("2021-08-08"), false, 299, 3, "сироп");
    Medicine test2 = new Medicine(2, "Гроприносин", 10, 111, "500 мг", getDate("2022-01-29"), true, 460, 41, "таблетки");
    Medicine test3 = new Medicine(3, "Кагоцел", 10, 41, "12 мг", getDate("2020-12-17"), false, 827, 14, "таблетки");
    Medicine test4 = new Medicine(1, "Парацетамол", 1, 39, "30 мг/мл", getDate("2021-08-08"), false, 299, 3, "сироп");
    Medicine test5 = new Medicine(1, "Парацетамол", 1, 39, "30 мг/мл", getDate("2021-08-08"), false, 299, 3, "сироп");

    MedicineTest() throws ParseException {
    }

    private Date getDate(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.parse(date);
    }

    @Test
    void isCustomerNeedRecipe() {
        test3.setCustomerNeedRecipe(true);
        assertTrue(test3.isCustomerNeedRecipe());
    }

    @Test
    void setCustomerNeedRecipe() {
        test3.setCustomerNeedRecipe(true);
        assertTrue(test3.isCustomerNeedRecipe());
    }

    @Test
    void getId() {
        assertEquals(1, test1.getId());
        assertEquals(2, test2.getId());
    }

    @Test
    void getName() {
        assertEquals("Парацетамол", test1.getName());
        assertEquals("Гроприносин", test2.getName());
    }

    @Test
    void getIndivisibleAmount() {
        assertEquals(1, test1.getIndivisibleAmount());
        assertEquals(10, test2.getIndivisibleAmount());
    }

    @Test
    void getAmount() {
        assertEquals(39, test1.getAmount());
        assertEquals(111, test2.getAmount());
    }

    @Test
    void getDosage() {
        assertEquals("30 мг/мл", test1.getDosage());
        assertEquals("500 мг", test2.getDosage());
    }

    @Test
    void getExpDate() throws ParseException {
        assertEquals(getDate("2021-08-08"), test1.getExpDate());
        assertEquals(getDate("2022-01-29"), test2.getExpDate());
    }

    @Test
    void isRecipeRequired() {
        assertFalse(test1.isRecipeRequired());
        assertTrue(test2.isRecipeRequired());
    }

    @Test
    void getPrice() {
        assertEquals(299, test1.getPrice());
        assertEquals(460, test2.getPrice());
    }

    @Test
    void getAddedBy() {
        assertEquals(3, test1.getAddedBy());
        assertEquals(41, test2.getAddedBy());
    }

    @Test
    void getPharmForm() {
        assertEquals("сироп", test1.getPharmForm());
        assertEquals("таблетки", test2.getPharmForm());
    }

    @Test
    void getRub() {
        assertEquals(2, test1.getRub());
        assertEquals(4, test2.getRub());
    }

    @Test
    void getCoin() {
        assertEquals(99, test1.getCoin());
        assertEquals(60, test2.getCoin());
    }

    @Test
    void getCountInCustomerCart() {
        test3.setCountInCustomerCart(4);
        assertEquals(4, test3.getCountInCustomerCart());
    }

    @Test
    void setCountInCustomerCart() {
        test3.setCountInCustomerCart(4);
        assertEquals(4, test3.getCountInCustomerCart());
    }

    @Test
    void setAmount() {
        test3.setAmount(989);
        assertEquals(989, test3.getAmount());
    }

    @Test
    void testEquals() {
        assertTrue(test1.equals(test4));
    }

    @Test
    void testHashCode() {
        assertEquals(test4.hashCode(), test5.hashCode());
    }
}