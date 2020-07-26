package by.petropavlovskaja.pharmacy.model;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {
    Order test1 = new Order(1, 78, 8756, getDate("2019-08-18"), true);
    Order test2 = new Order(71, 44, 587, getDate("2019-09-19"), false);
    Order test3 = new Order(71, 44, 587, getDate("2019-09-19"), false);
    Order test4 = new Order(264, 8, 56, getDate("2020-02-06"), false);
    Order test5 = new Order(264, 8, 56, getDate("2020-02-06"), false);

    OrderTest() throws ParseException {
    }

    private java.util.Date getDate(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.parse(date);
    }

    @Test
    void getId() {
        assertEquals(1, test1.getId());
        assertEquals(71, test2.getId());
    }

    @Test
    void getFkCustomer() {
        assertEquals(78, test1.getFkCustomer());
        assertEquals(44, test2.getFkCustomer());
    }

    @Test
    void getOrderPrice() {
        assertEquals(8756, test1.getOrderPrice());
        assertEquals(587, test2.getOrderPrice());
    }

    @Test
    void getOrderDate() throws ParseException {
        assertEquals(getDate("2019-08-18"), test1.getOrderDate());
        assertEquals(getDate("2019-09-19"), test2.getOrderDate());
    }

    @Test
    void isCart() {
        assertTrue(test1.isCart());
        assertFalse(test2.isCart());
    }

    @Test
    void getRub() {
        test1.setRub(87);
        test2.setRub(5);
        assertEquals(87, test1.getRub());
        assertEquals(5, test2.getRub());
    }

    @Test
    void setRub() {
        test3.setRub(1);
        assertEquals(1, test3.getRub());
    }

    @Test
    void getCoin() {
        test1.setCoin(56);
        test2.setCoin(87);
        assertEquals(56, test1.getCoin());
        assertEquals(87, test2.getCoin());
    }

    @Test
    void setCoin() {
        test3.setCoin(99);
        assertEquals(99, test3.getCoin());
    }

    @Test
    void testEquals() {
        assertTrue(test4.equals(test5));
    }

    @Test
    void testHashCode() {
        assertEquals(test5.hashCode(), test5.hashCode());
    }
}