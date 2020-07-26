package by.petropavlovskaja.pharmacy.model.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    Account test1 = new Account.AccountBuilder("Kuzin", "Dmitry", AccountRole.DOCTOR)
            .withId(1).withPatronymic("Gennadyevich").withPhoneNumber("+375(29)5556433").withStatus(true).build();
    Account test2 = new Account.AccountBuilder("Zobnin", "Eugeny", AccountRole.CUSTOMER)
            .withId(2).withPatronymic("Yuryevich").withPhoneNumber("+375(33)3336687").withStatus(false).build();
    Account test3 = new Account.AccountBuilder("Pertova", "Anna", AccountRole.DOCTOR)
            .withId(3).withPatronymic("Ivanovna").withPhoneNumber("+375(29)5555433").withStatus(true).build();

    @Test
    void setSurname() {
        test3.setSurname("Ivannikova");
        assertEquals("Ivannikova", test3.getSurname());
    }

    @Test
    void setName() {
        test3.setName("Vera");
        assertEquals("Vera", test3.getName());
    }

    @Test
    void setPatronymic() {
        test3.setPatronymic("Nikolayevna");
        assertEquals("Nikolayevna", test3.getPatronymic());
    }

    @Test
    void setPhoneNumber() {
        test3.setPhoneNumber("+375(33)5555433");
        assertEquals("+375(33)5555433", test3.getPhoneNumber());
    }

    @Test
    void getSurname() {
        assertEquals("Kuzin", test1.getSurname());
        assertEquals("Zobnin", test2.getSurname());
    }

    @Test
    void getName() {
        assertEquals("Dmitry", test1.getName());
        assertEquals("Eugeny", test2.getName());
    }

    @Test
    void getPatronymic() {
        assertEquals("Gennadyevich", test1.getPatronymic());
        assertEquals("Yuryevich", test2.getPatronymic());
    }

    @Test
    void getPhoneNumber() {
        assertEquals("+375(29)5556433", test1.getPhoneNumber());
        assertEquals("+375(33)3336687", test2.getPhoneNumber());
    }

    @Test
    void isActive() {
        assertTrue(test1.isActive());
        assertFalse(test2.isActive());
    }

    @Test
    void getId() {
        assertEquals(1, test1.getId());
        assertEquals(2, test2.getId());
    }

    @Test
    void getAccountRole() {
        assertEquals(AccountRole.DOCTOR, test1.getAccountRole());
        assertEquals(AccountRole.CUSTOMER, test2.getAccountRole());
    }
}