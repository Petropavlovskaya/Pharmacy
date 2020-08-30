package by.petropavlovskaja.pharmacy.service;

import by.petropavlovskaja.pharmacy.dao.AccountDAO;
import by.petropavlovskaja.pharmacy.model.account.Account;
import by.petropavlovskaja.pharmacy.model.account.AccountRole;
import by.petropavlovskaja.pharmacy.model.account.Customer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommonServiceTest {
    Account accountTestChangeData = new Account.AccountBuilder("Kuzin", "Dmitry", AccountRole.DOCTOR)
            .withId(1).withPatronymic("Gennadyevich").withPhoneNumber("+375(29)5556433").withStatus(true).build();

    Customer customerTestRegistration = new Customer(new Account.AccountBuilder("Zobnin", "Eugeny", AccountRole.CUSTOMER)
            .withId(2).withPatronymic("Yuryevich").withPhoneNumber("+375(33)3336687").withStatus(false));
    Map<String, Object> reqParameters = new HashMap<>();

    @BeforeEach
    public void initAccountParams() {
        reqParameters.put("login", "zobnin");
        reqParameters.put("password", "123456");
        reqParameters.put("passwordConfirm", "123456");
        reqParameters.put("accountSurname", "Zobnin");
        reqParameters.put("accountName", "Eugeny");
        reqParameters.put("accountPatronymic", "Yuryevich");
        reqParameters.put("accountPhone", "+375(33)3336687");
    }

    @AfterEach
    public void destroyAccountParams() {
        reqParameters.clear();
    }

    @Test
    void getInstance() {
        assertNotNull(CommonService.getInstance());
    }

    @Test
    void accountRegistration() {
        initAccountParams();
        AccountRole role = AccountRole.CUSTOMER;

        AccountDAO accMoc = Mockito.mock(AccountDAO.class);
        when(accMoc.create(Mockito.any(Account.class), Mockito.anyString(), Mockito.anyString())).thenReturn(true).thenReturn(false);
        when(accMoc.findCustomerByLogin(Mockito.anyString())).thenReturn(customerTestRegistration);

        String login = String.valueOf(reqParameters.get("login"));
        String password = String.valueOf(reqParameters.get("password"));
        String accountSurname = (String) reqParameters.get("accountSurname");
        String accountName = (String) reqParameters.get("accountName");
        String accountPatronymic = (String) reqParameters.get("accountPatronymic");
        String accountPhone = (String) reqParameters.get("accountPhone");

        Account account = new Account.AccountBuilder(accountSurname, accountName, role)
                .withPatronymic(accountPatronymic).withPhoneNumber(accountPhone).build();
        boolean insertResult = accMoc.create(account, login, password);
        if (!insertResult) {
            account = new Account(-1);
        } else {
            account = accMoc.findCustomerByLogin(login);
        }
        assertEquals(customerTestRegistration, account);

        insertResult = accMoc.create(account, login, password);
        if (!insertResult) {
            account = new Account(-1);
        } else {
            account = accMoc.findCustomerByLogin(login);
        }
        assertNotEquals(customerTestRegistration, account);
    }

    @Test
    void changeAccountData() {
        String accountSurname = (String) reqParameters.get("accountSurname");
        String accountName = (String) reqParameters.get("accountName");
        String accountPatronymic = (String) reqParameters.get("accountPatronymic");
        String accountPhone = (String) reqParameters.get("accountPhone");

        accountTestChangeData.setSurname(accountSurname);
        accountTestChangeData.setName(accountName);
        accountTestChangeData.setPatronymic(accountPatronymic);
        accountTestChangeData.setPhoneNumber(accountPhone);
        AccountDAO accMoc = Mockito.mock(AccountDAO.class);
        when(accMoc.changeAccountData(Mockito.anyInt(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(true);

        Account daoAccount = new Account.AccountBuilder("Kuzin", "Dmitry", AccountRole.DOCTOR)
                .withId(1).withPatronymic("Gennadyevich").withPhoneNumber("+375(29)5556433").withStatus(true).build();

        boolean successfulUpdate = accMoc.changeAccountData(2, accountSurname, accountName, accountPatronymic, accountPhone);
        if (successfulUpdate) {
            daoAccount.setSurname(accountSurname);
            daoAccount.setName(accountName);
            daoAccount.setPatronymic(accountPatronymic);
            daoAccount.setPhoneNumber(accountPhone);
        }

        assertEquals(accountTestChangeData, daoAccount);
    }

    @Test
    void changeAccountPassword() {
        String result = "noError";

        AccountDAO accMoc = Mockito.mock(AccountDAO.class);
        when(accMoc.checkLoginAndPassword(Mockito.anyString(), Mockito.anyString()))
                .thenReturn(accountTestChangeData).thenReturn(new Account(-1));
        CommonService commonMock = Mockito.mock(CommonService.class);
        when(commonMock.checkPasswordEquals(Mockito.anyString(), Mockito.anyString())).thenReturn(null);

        Account account = accMoc.checkLoginAndPassword("login", "accountOldPassword");
        if (account.getId() != -1) {
            String s = commonMock.checkPasswordEquals("accountNewPassword", "accountNewPasswordConfirm");
            if (s != null) {
                result = s;
            }
        } else {
            result = "Old password is incorrect!";
        }
        assertEquals("noError", result);

        account = accMoc.checkLoginAndPassword("login", "accountOldPassword");
        if (account.getId() != -1) {
            String s = commonMock.checkPasswordEquals("accountNewPassword", "accountNewPasswordConfirm");
            if (s != null) {
                result = s;
            }
        } else {
            result = "Old password is incorrect!";
        }
        assertEquals("Old password is incorrect!", result);
    }

    @Test
    void checkAccountDataBeforeCreate() {
        String patronymicError = "Account patronymic insert data is incorrect. Please, enter valid data and try again.";
        String nameError = "Account surname and/or name insert data are incorrect. Please, enter valid data and try again.";
        assertNull(CommonService.getInstance().checkAccountDataBeforeCreate(reqParameters));
        reqParameters.replace("accountPatronymic", "Vasil7jevich");
        assertEquals(patronymicError, CommonService.getInstance().checkAccountDataBeforeCreate(reqParameters));
        reqParameters.replace("accountName", "Zhe*nia");
        assertEquals(nameError, CommonService.getInstance().checkAccountDataBeforeCreate(reqParameters));
        reqParameters.replace("accountName", "Eugeny");
        reqParameters.replace("accountSurname", "$Zobnin");
        assertEquals(nameError, CommonService.getInstance().checkAccountDataBeforeCreate(reqParameters));
    }

    @Test
    void testCheckAccountDataBeforeCreate() {
        String loginEmptyError = "Login can't be empty. Please, enter login and try again.";

        String login = null;
        assertEquals(loginEmptyError, CommonService.getInstance().checkAccountDataBeforeCreate(reqParameters, login));
    }

    @Test
    void checkPasswordEquals() {
        String passwordEmptyError = "Password or/and password confirm can't be empty. Please, enter password or/and password confirm and try again.";
        String passwordEqualsError = "Password and password confirm are not equals. Please, try again.";

        String s = CommonService.getInstance().checkPasswordEquals("123456", "123456");
        assertNull(s);

        s = CommonService.getInstance().checkPasswordEquals("123456", null);
        assertEquals(passwordEmptyError, s);

        s = CommonService.getInstance().checkPasswordEquals(null, "123456");
        assertEquals(passwordEmptyError, s);

        s = CommonService.getInstance().checkPasswordEquals("123456", "123487");
        assertEquals(passwordEqualsError, s);
    }

    @Test
    void getCustomer() {
        AccountDAO daoMock = Mockito.mock(AccountDAO.class);
        when(daoMock.findCustomerById(Mockito.anyInt())).thenReturn(customerTestRegistration);

        Customer nullCustomer = new Customer(-1);
        assertEquals(nullCustomer, CommonService.getInstance().getCustomer(0));
        assertEquals(nullCustomer, CommonService.getInstance().getCustomer(-1));

        nullCustomer = new Customer(-1);
        int accountId = 2;
        if (accountId > 0) {
            nullCustomer = daoMock.findCustomerById(accountId);
        }
        assertEquals(customerTestRegistration, nullCustomer);
    }

    @Test
    void isStringDate() {
        String checkDate = "2020-08-34";
        assertFalse(CommonService.getInstance().isStringDate(checkDate));
        checkDate = "2029-08-00";
        assertFalse(CommonService.getInstance().isStringDate(checkDate));
        checkDate = "2020-02-30";
        assertFalse(CommonService.getInstance().isStringDate(checkDate));
        checkDate = "2019-02-29";
        assertFalse(CommonService.getInstance().isStringDate(checkDate));

        checkDate = "202-08-30";
        try {
            CommonService.getInstance().isStringDate(checkDate);
        } catch (Exception e) {
            assertTrue(true);
        }

        checkDate = "20202-08-30";
        try {
            CommonService.getInstance().isStringDate(checkDate);
        } catch (Exception e) {
            assertTrue(true);
        }

        checkDate = "2020-08-12";
        assertTrue(CommonService.getInstance().isStringDate(checkDate));
        checkDate = "2018-12-31";
        assertTrue(CommonService.getInstance().isStringDate(checkDate));
        checkDate = "2001-01-01";
        assertTrue(CommonService.getInstance().isStringDate(checkDate));
    }

    @Test
    void isNumber() {
        assertFalse(CommonService.getInstance().isNumber("044"));
        assertFalse(CommonService.getInstance().isNumber("s44"));
        assertFalse(CommonService.getInstance().isNumber("4454d"));
        assertFalse(CommonService.getInstance().isNumber("445-88"));
        assertTrue(CommonService.getInstance().isNumber("88"));
        assertTrue(CommonService.getInstance().isNumber("105"));
        assertTrue(CommonService.getInstance().isNumber("1"));
        assertTrue(CommonService.getInstance().isNumber("1024"));
    }

    @Test
    void isPriceNumber() {
        assertFalse(CommonService.getInstance().isPriceNumber("0444", "90"));
        assertFalse(CommonService.getInstance().isPriceNumber("04", "100"));
        assertFalse(CommonService.getInstance().isPriceNumber("4454d", "55"));
        assertFalse(CommonService.getInstance().isPriceNumber("445-88", "3"));
        assertTrue(CommonService.getInstance().isPriceNumber("88", "01"));
        assertTrue(CommonService.getInstance().isPriceNumber("10", "12"));
        assertTrue(CommonService.getInstance().isPriceNumber("01", "08"));
        assertTrue(CommonService.getInstance().isPriceNumber("102", "99"));
    }

    @Test
    void getFutureDate() {
        Date date = CommonService.getInstance().getFutureDate(1);
        String expectDate = CommonService.getInstance().getStringDate(1);
        String getDate;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        getDate = format.format(date);
        assertEquals(expectDate, getDate);

        date = CommonService.getInstance().getFutureDate(10);
        expectDate = CommonService.getInstance().getStringDate(10);
        getDate = format.format(date);
        assertEquals(expectDate, getDate);
    }

    @Test
    void getStringDate() {
        String testDate1 = CommonService.getInstance().getStringDate(10);
        String testDate2 = CommonService.getInstance().getStringDate(8);
        assertTrue(testDate2.compareTo(testDate1) < 0);
    }

    @Test
    void loginMatchRegex() {
        String result = null;
        String login = "Olga77";
        if (!CommonService.getInstance().loginMatchRegex(login)) {
            result = "Account login insert data is incorrect. Please, enter valid data and try again.";
        }
        assertNull(result);

        login = "7Olga7";
        String loginMatchError = "Account login insert data is incorrect. Please, enter valid data and try again.";
        if (!CommonService.getInstance().loginMatchRegex(login)) {
            result = "Account login insert data is incorrect. Please, enter valid data and try again.";
        }
        assertEquals(loginMatchError, result);
    }

    @Test
    void fioMatchRegex() {
        assertFalse(CommonService.getInstance().fioMatchRegex("Я"));
        assertFalse(CommonService.getInstance().fioMatchRegex("Яв7ин"));
        assertFalse(CommonService.getInstance().fioMatchRegex("Яв*ин"));
        assertFalse(CommonService.getInstance().fioMatchRegex("Yt3kk"));
        assertFalse(CommonService.getInstance().fioMatchRegex("somov"));
        assertFalse(CommonService.getInstance().fioMatchRegex("Somov-"));

        assertTrue(CommonService.getInstance().fioMatchRegex("Somov"));
        assertTrue(CommonService.getInstance().fioMatchRegex("Як"));
        assertTrue(CommonService.getInstance().fioMatchRegex("Ilyn"));
        assertTrue(CommonService.getInstance().fioMatchRegex("Inanov-Kulic"));
        assertTrue(CommonService.getInstance().fioMatchRegex("Yan Czen"));
    }

    @Test
    void isLoginBusy() {
        boolean b = CommonService.getInstance().isLoginBusy("Df");
        assertFalse(b);

        b = CommonService.getInstance().isLoginBusy("PrazdnichnyjYozhyk");
        assertFalse(b);
    }
}