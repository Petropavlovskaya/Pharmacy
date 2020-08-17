package by.petropavlovskaja.pharmacy.service;

import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.controller.session.SessionContext;
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
import java.util.regex.Pattern;

import static by.petropavlovskaja.pharmacy.controller.AttributeConstant.*;

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
        String login = String.valueOf(reqParameters.get(LOGIN));
        String password = String.valueOf(reqParameters.get(PASSWORD));
        String accountSurname = (String) reqParameters.get(ACCOUNT_SURNAME);
        String accountName = (String) reqParameters.get(ACCOUNT_NAME);
        String accountPatronymic = (String) reqParameters.get(ACCOUNT_PATRONYMIC);
        String accountPhone = (String) reqParameters.get(ACCOUNT_PHONE);

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
     * The method of changing customer information  {@link AccountDAO#changeAccountData(int, String, String, String, String)},
     * {@link SessionContext}
     *
     * @param account       - account instance
     * @param reqParameters - customer request parameters
     * @param accountId     - customer ID
     * @param sc            - SessionContext instance
     * @return - true if changing was successful
     */
    public boolean changeAccountData(Account account, Map<String, Object> reqParameters, int accountId, SessionContext sc) {
        String accountSurname = (String) reqParameters.get(ACCOUNT_SURNAME);
        String accountName = (String) reqParameters.get(ACCOUNT_NAME);
        String accountPatronymic = (String) reqParameters.get(ACCOUNT_PATRONYMIC);
        String accountPhone = (String) reqParameters.get(ACCOUNT_PHONE);

        boolean accountUpdate = false;
        String fioError = checkAccountDataBeforeCreate(reqParameters);
        if (fioError == null) {
            sc.getSession().setAttribute(SUCCESS_MSG, "The personal info was change successfully.");
            sc.getSession().setAttribute(SUCCESS_MSG_CHECK, "yes");
            accountUpdate = accountDAO.changeAccountData(accountId, accountSurname, accountName, accountPatronymic, accountPhone);
        }

        if (accountUpdate) {
            account.setSurname(accountSurname);
            account.setName(accountName);
            account.setPatronymic(accountPatronymic);
            account.setPhoneNumber(accountPhone);
        }
        return accountUpdate;
    }

    /**
     * The method of changing customer information  {@link AccountDAO#changeAccountData(int, String, String, String, String)}
     *
     * @param reqParameters - customer request parameters
     * @param login         - account login
     * @return - error message or NULL
     */
    public String changeAccountPassword(Map<String, Object> reqParameters, String login) {
        String result = "noError";
        String accountOldPassword = (String) reqParameters.get(OLD_PASSWORD);
        String accountNewPassword = (String) reqParameters.get(NEW_PASSWORD);
        String accountNewPasswordConfirm = (String) reqParameters.get(NEW_PASSWORD_CONFIRM);
        Account account = accountDAO.checkLoginAndPassword(login, accountOldPassword);
        if (account.getId() != -1) {
            String s = checkPasswordEquals(accountNewPassword, accountNewPasswordConfirm);
            if (s != null) {
                result = s;
            } else {
                String accountSalt = accountDAO.getSaltByLogin(login);
                String newDbPassword = AccountDAO.getMd5Password(accountNewPassword, accountSalt);
                boolean insertSuccess = accountDAO.setNewAccountPassword(login, newDbPassword);
                if (!insertSuccess) {
                    result = "Error. Can't insert into database. Please, contact with site administrator.";
                }
            }
        } else {
            result = "Old password is incorrect!";
        }
        return result;
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
        String password = String.valueOf(reqParameters.get(PASSWORD));
        String passwordConfirm = String.valueOf(reqParameters.get(PASSWORD_CONFIRM));

        if (login == null) {
            result = "Login can't be empty. Please, enter login and try again.";
        } else if (isLoginBusy(login)) {
            result = "This login already busy. Please, choose another login and try again.";
        } else if (!loginMatchRegex(login)) {
            result = "Account login insert data is incorrect. Please, enter valid data and try again.";
        }
        String s = checkPasswordEquals(password, passwordConfirm);
        if (s != null) {
            result = s;
        }
        String fioError = checkAccountDataBeforeCreate(reqParameters);
        if (fioError != null) {
            result = fioError;
        }
        return result;
    }


    /**
     * The method for checking account fio data before account creates
     *
     * @param reqParameters - customer request parameters
     * @return - string of error message or NULL
     */
    public String checkAccountDataBeforeCreate(Map<String, Object> reqParameters) {
        String result = null;
        String surname = (String) reqParameters.get("accountSurname");
        String name = (String) reqParameters.get("accountName");
        if (!fioMatchRegex(surname) || !fioMatchRegex(name)) {
            result = "Account surname and/or name insert data are incorrect. Please, enter valid data and try again.";
        } else if (reqParameters.get("accountPatronymic") != null) {
            String patronymic = (String) reqParameters.get("accountPatronymic");
            if (!fioMatchRegex(patronymic)) {
                result = "Account patronymic insert data is incorrect. Please, enter valid data and try again.";
            }
        }
        return result;
    }


    /**
     * The method for checking account data before account creates
     *
     * @param password        - new account password
     * @param passwordConfirm - confirm of new account password
     * @return - string of error message or NULL
     */
    public String checkPasswordEquals(String password, String passwordConfirm) {
        String result = null;

        if (password == null || passwordConfirm == null) {
            result = "Password or/and password confirm can't be empty. Please, enter password or/and password confirm and try again.";
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
    boolean isLoginBusy(String login) {
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
            logger.trace("Parse exception for request date ", e);
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
        return format.format(getFutureDate(plusDays));
    }

    /**
     * The method for getting an account instance
     *
     * @param accountId - account ID
     * @return - account instance
     */
    public Account getAccount(int accountId) {
        return accountDAO.find(accountId);
    }

    public boolean changePassword(Map<String, Object> reqParameters, ExecuteResult executeResult, String login, SessionContext sc) {
        boolean changeResult = false;
        String errorMessage = changeAccountPassword(reqParameters, login);
        if (errorMessage.equals("noError")) {
            sc.getSession().setAttribute("successMessage", "The password has changed successfully.");
            sc.getSession().setAttribute("successTextSet", "yes");
            changeResult = true;
        } else {
            executeResult.setResponseAttributes("errorMessage", errorMessage);
        }
        return changeResult;
    }

    /**
     * The method for checking login against regular expression
     *
     * @param login - account login
     * @return - true if login against regular expression
     */
    public boolean loginMatchRegex(String login) {
        String regex = "[A-Za-z]+[A-Za-z0-9]{3,15}";
        return Pattern.matches(regex, login);
    }

    /**
     * The method for checking login against regular expression
     *
     * @param info - account surname/name/patronymic
     * @return - true if info against regular expression
     */
    public boolean fioMatchRegex(String info) {
        String regex = "(([A-ZА-Я][a-zа-я]{1,20})([-\\s][A-ZА-Я][a-zа-я]{1,20})*)+?";
        return Pattern.matches(regex, info);
    }

    /**
     * The method check is success message set in previous command.
     *
     * @param sc - Session context {@link SessionContext}
     */

    public void checkSuccessMessageSet(SessionContext sc) {
        if (sc.getSession().getAttribute(SUCCESS_MSG_CHECK) != null) {
            if (sc.getSession().getAttribute(SUCCESS_MSG_CHECK).equals("yes")) {
                sc.getSession().setAttribute(SUCCESS_MSG_CHECK, "no");
            } else {
                sc.getSession().removeAttribute(SUCCESS_MSG);
            }
        }
    }

}
