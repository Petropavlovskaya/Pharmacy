package by.petropavlovskaja.pharmacy.model;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class RecipeTest {
    Recipe test1 = new Recipe(1, "New", "15/26", 44, 1, 184, getDate("2020-10-20"), true);
    Recipe test2 = new Recipe(7, "Гроприносин", "500 мг", 73, 16, 12, getDate("2020-11-03"), false);
    Recipe test3 = new Recipe(8, "Гроприносин", "500 мг", 2, 5, 1, getDate("2020-11-03"), false);
    Recipe test4 = new Recipe(8, "Гроприносин", "500 мг", 2, 5, 1, getDate("2020-11-03"), false);
    Recipe test5 = new Recipe(8, "Гроприносин", "500 мг", 2, 5, 1, getDate("2020-11-03"), false);

    RecipeTest() throws ParseException {
    }

    private Date getDate(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.parse(date);
    }

    @Test
    void getId() {
        assertEquals(1, test1.getId());
        assertEquals(7, test2.getId());
    }

    @Test
    void setId() {
        test3.setId(12);
        assertEquals(12, test3.getId());
    }

    @Test
    void getMedicine() {
        assertEquals("New", test1.getMedicine());
        assertEquals("Гроприносин", test2.getMedicine());
    }

    @Test
    void setMedicine() {
        test3.setMedicine("TEST");
        assertEquals("TEST", test3.getMedicine());
    }

    @Test
    void getDosage() {
        assertEquals("15/26", test1.getDosage());
        assertEquals("500 мг", test2.getDosage());
    }

    @Test
    void setDosage() {
        test3.setDosage("newDosage");
        assertEquals("newDosage", test3.getDosage());
    }
    @Test
    void getDoctorID() {
        assertEquals(44, test1.getDoctorID());
        assertEquals(73, test2.getDoctorID());
    }

    @Test
    void setDoctorID() {
        test3.setDoctorID(64);
        assertEquals(64, test3.getDoctorID());
    }

    @Test
    void getFkCustomer() {
        assertEquals(1, test1.getFkCustomer());
        assertEquals(16, test2.getFkCustomer());
    }

    @Test
    void setFkCustomer() {
        test3.setFkCustomer(88);
        assertEquals(88, test3.getFkCustomer());
    }

    @Test
    void getIdMedicineInOrder() {
        assertEquals(184, test1.getIdMedicineInOrder());
        assertEquals(12, test2.getIdMedicineInOrder());

    }

    @Test
    void setIdMedicineInOrder() {
        test3.setIdMedicineInOrder(33);
        assertEquals(33, test3.getIdMedicineInOrder());
    }

    @Test
    void getValidity() throws ParseException {
        assertEquals(getDate("2020-10-20"), test1.getValidity());
        assertEquals(getDate("2020-11-03"), test2.getValidity());
    }

    @Test
    void setValidity() throws ParseException {
        test3.setValidity(getDate("2023-02-15"));
        assertEquals(getDate("2023-02-15"), test3.getValidity());
    }

    @Test
    void isNeedExtension() {
        assertTrue(test1.isNeedExtension());
        assertFalse(test2.isNeedExtension());
    }

    @Test
    void setNeedExtension() {
        test3.setNeedExtension(true);
        assertTrue(test3.isNeedExtension());
    }

    @Test
    void testEquals() {
        assertTrue(test4.equals(test5));
    }

    @Test
    void getCustomerFio() {
        test3.setCustomerFio("Ivanov Ivan");
        assertEquals("Ivanov Ivan", test3.getCustomerFio());
    }

    @Test
    void setCustomerFio() {
        test4.setCustomerFio("Ivanov Ivan Petrovich");
        assertEquals("Ivanov Ivan Petrovich", test4.getCustomerFio());
    }

    @Test
    void testHashCode() {
        assertEquals(test1.hashCode(), test1.hashCode());
        assertEquals(test2.hashCode(), test2.hashCode());
    }
}