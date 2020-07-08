package by.petropavlovskaja.pharmacy.service;

import by.petropavlovskaja.pharmacy.dao.AccountDAO;
import by.petropavlovskaja.pharmacy.dao.MedicineDAO;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.model.account.Account;
import by.petropavlovskaja.pharmacy.model.account.AccountRole;
import by.petropavlovskaja.pharmacy.model.account.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Class for common services. Uses {@link MedicineDAO} and {@link AccountDAO}
 */
public class CommonService {
    private static Logger logger = LoggerFactory.getLogger(CommonService.class);
    MedicineDAO medicineDAO = MedicineDAO.getInstance();
    AccountDAO accountDAO = AccountDAO.getInstance();

    /**
     * Constructor without parameters
     */
    private CommonService() {
    }

    /**
     * Nested class create instance of the class
     */
    private static class CommonServiceHolder {
        public static final CommonService COMMON_SERVICE = new CommonService();
    }

    /**
     * The method for get instance of the class
     *
     * @return - class instance
     */
    public static CommonService getInstance() {
        return CommonServiceHolder.COMMON_SERVICE;
    }

    /**
     * The method of getting all medicines {@link MedicineDAO#getAll()}
     *
     * @return - a medicine list
     */
    public List<Medicine> getAllMedicine() {
        return medicineDAO.getAll();
    }

    /**
     * The method of getting an account by login and password {@link AccountDAO#checkLoginAndPassword(String, String)}
     *
     * @param login    - account login
     * @param password - account password
     * @return - an account
     */
    public Account accountAuthentication(String login, String password) {
        return accountDAO.checkLoginAndPassword(login, password);
    }

    /**
     * The method of account registration  {@link AccountDAO#checkLoginAndPassword(String, String)},
     * {@link AccountDAO#findCustomerByLogin(String)}
     *
     * @param accountRole   - account role
     * @param reqParameters - request parameters
     * @return - a created account
     */
    public Account accountRegistration(Map<String, Object> reqParameters, AccountRole accountRole) {
        String login = String.valueOf(reqParameters.get("login"));
        String password = String.valueOf(reqParameters.get("password"));
        String accountSurname = (String) reqParameters.get("accountSurname");
        String accountName = (String) reqParameters.get("accountName");
        String accountPatronymic = (String) reqParameters.get("accountPatronymic");
        String accountPhone = (String) reqParameters.get("accountPhone");

        Account account = new Account.AccountBuilder(accountSurname, accountName, accountRole)
                .withPatronymic(accountPatronymic).withPhoneNumber(accountPhone).build();
        boolean insertResult = accountDAO.create(account, login, password);
        if (!insertResult) {
            account = new Account(-1);
        } else {
            account = accountDAO.findCustomerByLogin(login);
        }
        return account;
    }

    /**
     * The method of changing customer information  {@link AccountDAO#changeAccountData(int, String, String, String, String)}
     *
     * @param customer      - customer
     * @param reqParameters - customer request parameters
     * @param accountId     - customer ID
     * @return - true if changing was successful
     */
    public boolean changeAccountData(Customer customer, Map<String, Object> reqParameters, int accountId) {
        String accountSurname = (String) reqParameters.get("accountSurname");
        String accountName = (String) reqParameters.get("accountName");
        String accountPatronymic = (String) reqParameters.get("accountPatronymic");
        String accountPhone = (String) reqParameters.get("accountPhone");
        boolean successfulUpdate = accountDAO.changeAccountData(accountId, accountSurname, accountName, accountPatronymic, accountPhone);
        if (successfulUpdate) {
            customer.setSurname(accountSurname);
            customer.setName(accountName);
            customer.setPatronymic(accountPatronymic);
            customer.setPhoneNumber(accountPhone);
            return true;
        }
        return false;
    }

    /**
     * The method for checking account data before account creates
     *
     * @param login         - account ligin
     * @param reqParameters - customer request parameters
     * @return - string of error message or NULL
     */
    public String checkAccountDataBeforeCreate(Map<String, Object> reqParameters, String login) {
        String result = null;
        String password = String.valueOf(reqParameters.get("password"));
        String passwordConfirm = String.valueOf(reqParameters.get("passwordConfirm"));

        if (login == null || password == null) {
            result = "Login or/and password can't be empty. Please, enter login or/and password and try again.";
        } else if (isLoginBusy(login)) {
            result = "This login already busy. Please, choose another login and try again.";
        } else if (!password.equals(passwordConfirm)) {
            result = "Password and password confirm are not equals. Please, try again.";
        }
        return result;
    }

    /**
     * The method for checking is the picked login already exist in database
     *
     * @param login - account ligin
     * @return - true if the login already exist in database
     */
    private boolean isLoginBusy(String login) {
        boolean result = false;
        if ((login.length() > 3) && (login.length() < 16)) {
            result = AccountDAO.getInstance().isLoginBusy(login);
        }
        return result;
    }

    /**
     * The method for finding a customer entity from database by customer ID
     *
     * @param accountId - customer ID
     * @return - customer entity
     */
    public Customer getCustomer(int accountId) {
        Customer customer = new Customer(-1);
        if (accountId > 0) {
            customer = accountDAO.findCustomerById(accountId);
        }
        return customer;
    }

    /**
     * The method for checking is the entered string a date
     *
     * @param requestDate - an entered string
     * @return - true if the string is a date
     */
    public boolean isStringDate(String requestDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dateFormat.setLenient(false);
            dateFormat.parse(requestDate);
            return true;
        } catch (ParseException e) {
            logger.error("Parse exception for date = " + requestDate + ". Error: " + e);
            return false;
        }
    }

    /**
     * The method for checking is the entered string a number
     *
     * @param numberString - an entered string
     * @return - true if the string is a number
     */
    public boolean isNumber(String numberString) {
        return numberString.matches("[1-9][0-9]*");
    }

    /**
     * The method for checking is the entered strings are valid values of rub and coin
     *
     * @param rub - an entered rub string
     * @param kop - an entered coin string
     * @return - true if the strings are valid
     */
    public boolean isPriceNumber(String rub, String kop) {
        return rub.matches("[0-9]{1,3}") && kop.matches("[0-9]{1,2}");
    }

    /**
     * The method for getting the future required date
     *
     * @param plusDays - number of days before required date
     * @return - required date
     */
    public Date getFutureDate(int plusDays) {
        Date requiredDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(requiredDate);
        cal.add(Calendar.DATE, plusDays);
        requiredDate = cal.getTime();
        return requiredDate;
    }
    /**
     * The method for getting the future required date as string
     *
     * @param plusDays - number of days before required date
     * @return - required date as string
     */
    public String getStringDate(int plusDays) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String newDate = format.format(getFutureDate(plusDays));
        return newDate;
    }
}
