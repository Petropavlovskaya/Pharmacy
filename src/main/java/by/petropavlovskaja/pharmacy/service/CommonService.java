package by.petropavlovskaja.pharmacy.service;

import by.petropavlovskaja.pharmacy.controller.result.ExecuteResult;
import by.petropavlovskaja.pharmacy.dao.AccountDAO;
import by.petropavlovskaja.pharmacy.dao.MedicineDAO;
import by.petropavlovskaja.pharmacy.dao.OrderDAO;
import by.petropavlovskaja.pharmacy.dao.sql.AccountSQL;
import by.petropavlovskaja.pharmacy.model.Medicine;
import by.petropavlovskaja.pharmacy.model.Recipe;
import by.petropavlovskaja.pharmacy.model.account.Account;
import by.petropavlovskaja.pharmacy.model.account.AccountRole;
import by.petropavlovskaja.pharmacy.model.account.Customer;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class CommonService {
    MedicineDAO medicineDAO = MedicineDAO.getInstance();
    AccountDAO accountDAO = AccountDAO.getInstance();

    private CommonService() {
    }

    private static class CommonServiceHolder {
        public static final CommonService COMMON_SERVICE = new CommonService();
    }

    public static CommonService getInstance() {
        return CommonServiceHolder.COMMON_SERVICE;
    }


    public Set<Medicine> getAllMedicine() {
        return medicineDAO.getAll();
    }

    public Account accountAuthentication(String login, String password) {
        Account account = accountDAO.checkLoginAndPassword(login, password);
        return account;
    }

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
        }
        else {
            account = accountDAO.findCustomerByLogin(login);
        }
        return account;
    }

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

    private boolean isLoginBusy(String login) {
        boolean result = false;
        if ((login.length() > 3) && (login.length() < 16)) {
            result = AccountDAO.getInstance().isLoginBusy(login);
        }
        return result;
    }

    public Account getAccount(int accountId) {
        Account account = new Account(-1);
        if (accountId>0) {
            account = accountDAO.find(accountId);
        }
        return account;
    }

    public Customer getCustomer(int accountId) {
        Customer customer = new Customer(-1);
        if (accountId>0) {
            customer = accountDAO.findCustomerById(accountId);
        }
        return customer;
    }


}
