package by.petropavlovskaja.pharmacy.model.account;

import by.petropavlovskaja.pharmacy.model.Order;
import by.petropavlovskaja.pharmacy.model.Recipe;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {
    Account.AccountBuilder testBuilder1 = new Account.AccountBuilder("Kuzin", "Dmitry", AccountRole.CUSTOMER)
            .withId(1).withPatronymic("Gennadyevich").withPhoneNumber("+375(29)5556433").withStatus(true);
    Account.AccountBuilder testBuilder2 = new Account.AccountBuilder("Zobnin", "Eugeny", AccountRole.CUSTOMER)
            .withId(2).withPatronymic("Yuryevich").withPhoneNumber("+375(33)3336687").withStatus(false);
    Account.AccountBuilder testBuilder3 = new Account.AccountBuilder("Pertova", "Anna", AccountRole.CUSTOMER)
            .withId(3).withPatronymic("Ivanovna").withPhoneNumber("+375(29)5555433").withStatus(true);

    Customer test1 = new Customer(testBuilder1, 5869);
    Customer test2 = new Customer(testBuilder2, 88);
    Customer test3 = new Customer(testBuilder3, 155);

        @Test
        void getBalanceRub () {
            assertEquals(58, test1.getBalanceRub());
            assertEquals(0, test2.getBalanceRub());
        }

        @Test
        void getBalanceCoin () {
            assertEquals(58, test1.getBalanceRub());
            assertEquals(0, test2.getBalanceRub());
        }

        @Test
        void getBalance () {
            assertEquals(58, test1.getBalanceRub());
            assertEquals(0, test2.getBalanceRub());
        }

        @Test
        void getCart () {
            test3.setCart(new Order(5));
            assertEquals(new Order(5), test3.getCart());
        }

        @Test
        void setBalance () {
            test3.setBalance(8756);
            assertEquals(8756, test3.getBalance());
        }

        @Test
        void setCart () {
            test3.setCart(new Order(5));
            assertEquals(new Order(5), test3.getCart());
        }

    }