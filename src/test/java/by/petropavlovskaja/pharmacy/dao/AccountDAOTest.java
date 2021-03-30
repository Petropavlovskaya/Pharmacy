package by.petropavlovskaja.pharmacy.dao;

import by.petropavlovskaja.pharmacy.EntityRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountDAOTest {
/*    Connection connectionForTest;
    @BeforeEach
    void getConection() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        connectionForTest = DriverManager.getConnection("jdbc:postgresql://localhost:5432/pharmacy_test", "olesia", "olesia");
                ConnectionPool moc = Mockito.mock(ConnectionPool.class);
        when(moc.retrieveConnection()).thenReturn(connectionForTest);
    }
    @AfterEach
    void closeConnection() throws SQLException {
        connectionForTest.close();
    }
*/

    @Test
    void find() {
        assertEquals(-1, AccountDAO.getInstance().find(7).getId());
        assertNotEquals(7, AccountDAO.getInstance().find(7).getId());
        assertEquals(5, AccountDAO.getInstance().find(5).getId());
    }

    @Test
    void checkLoginAndPassword() {
        assertNotEquals(EntityRepository.ACCOUNT1, AccountDAO.getInstance().checkLoginAndPassword("Zoba", "1"));
        assertEquals("Eugeny", AccountDAO.getInstance().checkLoginAndPassword("Zoba", "eugen").getName());
        assertEquals(-1, AccountDAO.getInstance().checkLoginAndPassword("Zoba", "eugn").getId());
    }

    @Test
    void isLoginBusy() {
        assertFalse(AccountDAO.getInstance().isLoginBusy("Allesia"));
        assertTrue(AccountDAO.getInstance().isLoginBusy("Zoba"));
        assertFalse(AccountDAO.getInstance().isLoginBusy("Zobba"));
    }

    @Test
    void getActiveCustomers() {
        assertEquals(4, AccountDAO.getInstance().getActiveCustomers().size());
    }

    @Test
    void create() {
    }

    @Test
    void changeAccountData() {
    }

    @Test
    void findCustomerById() {
    }

    @Test
    void findCustomerByLogin() {
    }

    @Test
    void getCustomerBalance() {
        assertEquals(0, AccountDAO.getInstance().getCustomerBalance(3));
        assertNotNull(AccountDAO.getInstance().getCustomerBalance(2));
    }

    @Test
    void getMd5Password() {
        assertEquals("1c6fbc776e29068e0129d520f198b8d7", AccountDAO.getInstance().getMd5Password("eugen", "hDrai,^`"));
    }

    @Test
    void increaseCustomerBalance() {
    }

    @Test
    void getSaltByLogin() {
        assertEquals("hDrai,^`", AccountDAO.getInstance().getSaltByLogin("Zoba"));
    }

    @Test
    void setNewAccountPassword() {
    }
}