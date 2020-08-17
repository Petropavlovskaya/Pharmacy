package by.petropavlovskaja.pharmacy.dao;

import by.petropavlovskaja.pharmacy.configuration.ApplicationConfiguration;
import by.petropavlovskaja.pharmacy.dao.sql.AccountSQL;
import by.petropavlovskaja.pharmacy.db.ConnectionPool;
import by.petropavlovskaja.pharmacy.model.account.Account;
import by.petropavlovskaja.pharmacy.model.account.AccountRole;
import by.petropavlovskaja.pharmacy.model.account.Customer;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import static by.petropavlovskaja.pharmacy.dao.DatabaseColumnNameConstant.*;

/**
 * Class for executing SQL queries to the database related to the account
 */
public class AccountDAO {
    /**
     * Property logger (log4j is uses)
     */
    private static Logger logger = LoggerFactory.getLogger(AccountDAO.class);

    /**
     * Property for getting a random integer
     */
    private static Random random;

    static {
        try {
            random = SecureRandom.getInstanceStrong();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Exception in a static block of AccountDAO. ", e);
        }
    }

    /**
     * Property String constant password
     */
    private static final String PASSWORD = "password";

    /**
     * Constructor - create INSTANCE of class
     */
    private AccountDAO() {
    }

    /**
     * Nested class create instance of the class
     */
    private static class AccountDAOHolder {
        public static final AccountDAO ACCOUNT_DAO = new AccountDAO();
    }

    /**
     * The method for get instance of the class
     * The method for get instance of the class
     *
     * @return - class instance
     */
    public static AccountDAO getInstance() {
        return AccountDAOHolder.ACCOUNT_DAO;
    }

    /**
     * The method finds an account in the database
     *
     * @param id - account ID
     * @return - account instance
     */
    public Account find(int id) {
        return findBy(AccountSQL.FIND_ACCOUNT_BY_ID.getQuery(), id);
    }

    /**
     * The method checks match account login and password for login to the application
     *
     * @param login    - account login
     * @param password - account password
     * @return - Account instance from the database if parameters are match or Account with ID = -1 if doesn't match
     */
    public Account checkLoginAndPassword(String login, String password) {
        String loggerMessage;
        Account checkedAccount = new Account(-1);
        Map<String, String> passwordAndSalt = getUserPasswordAndSaltFromDB(login);

        if (!passwordAndSalt.isEmpty()) {
            String dbPassword = passwordAndSalt.get(PASSWORD);
            String dbSalt = passwordAndSalt.get("salt");
            String md5Password = getMd5Password(password, dbSalt);
            loggerMessage = "bdPassword = " + dbPassword + " and dbSalt = " + dbSalt + " md5Password = " + md5Password;
            logger.info(loggerMessage);

            if (dbPassword.equals(md5Password)) {
                logger.info("Try to return logged account");
                checkedAccount = findBy(AccountSQL.FIND_ACCOUNT_BY_LOGIN.getQuery(), login);
                logger.info("Return logged account");
            }
        } else {
            logger.info("Password & Salt are empty!");
        }
        return checkedAccount;
    }

    /**
     * The method checks is login already exist in the database
     *
     * @param login - account login
     * @return - true if login is busy
     */
    public boolean isLoginBusy(String login) {
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(AccountSQL.FIND_LOGIN.getQuery())

        ) {
            statement.setString(1, login);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return true;
                }
            }
        } catch (
                SQLException e) {
            logger.trace("SQL exception in a method isLoginBusy. ", e);
        }
        return false;
    }

    /**
     * The method of getting a set of all active customers in the database
     *
     * @return - active customers
     */
    public Set<Account> getActiveCustomers() {
        Comparator<Account> comparator = new Account.AccountSurnameComparator()
                .thenComparing(new Account.AccountNameComparator())
                .thenComparing(new Account.AccountPatronymicComparator())
                .thenComparing(new Account.AccountPhoneComparator());
        Set<Account> accountSet = new TreeSet<>(comparator);
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(AccountSQL.GET_ALL_CUSTOMERS.getQuery())
        ) {
            while (rs.next()) {
                Account.AccountBuilder accountBuilder = new Account.AccountBuilder(
                        rs.getString(ACCOUNT_SURNAME),
                        rs.getString(ACCOUNT_NAME),
                        AccountRole.CUSTOMER)
                        .withId(rs.getInt(ACCOUNT_ID))
                        .withPatronymic(rs.getString(ACCOUNT_PATRONYMIC))
                        .withPhoneNumber(rs.getString(ACCOUNT_PHONE));
                accountSet.add(accountBuilder.build());
            }

        } catch (
                SQLException e) {
            logger.trace("SQL exception in a method getActiveCustomers. ", e);
            e.printStackTrace();
        }
        return accountSet;
    }

    /**
     * The method inserts new account into the database
     *
     * @param account  - account
     * @param login    - account login
     * @param password - account password
     * @return - true if write to the database was successful
     */
    public boolean create(Account account, String login, String password) {
        String errorMessage;
        String infoMessage;
        boolean resultAccountSet = false;
        int countInsertRowsLogin;
        int userId;
        Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
        Savepoint savepoint = null;

        try (
                PreparedStatement statementLogin = conn.prepareStatement(AccountSQL.INSERT_LOGIN.getQuery(), Statement.RETURN_GENERATED_KEYS);
                PreparedStatement statementAccount = conn.prepareStatement(AccountSQL.INSERT_ACCOUNT.getQuery())
        ) {
            conn.setAutoCommit(false);
            savepoint = conn.setSavepoint("Create Login");
            logger.info("Account autocommit false, savepoint was set successfully");

// Create login & password
            String salt = generateRandomSalt();
            int columnNumberLogin = 1;
            statementLogin.setString(columnNumberLogin++, login);
            statementLogin.setString(columnNumberLogin++, getMd5Password(password, salt));
            statementLogin.setString(columnNumberLogin, salt);
            countInsertRowsLogin = statementLogin.executeUpdate();
            if (countInsertRowsLogin != 1) {
                conn.rollback(savepoint);
                errorMessage = "Insert into table Login has failed. We insert: " + countInsertRowsLogin + " rows for login: " + login;
                logger.error(errorMessage);
            } else {
                ResultSet resultSet = statementLogin.getGeneratedKeys();
                if (resultSet.next()) {
                    userId = resultSet.getInt(1);
                    infoMessage = "Insert into table Login complete. Login " + login + " is added. UserId is: " + userId;
                    logger.info(infoMessage);

// Crete account data
                    int columnNumberAccount = 1;
                    statementAccount.setInt(columnNumberAccount++, userId);
                    statementAccount.setString(columnNumberAccount++, account.getSurname());
                    statementAccount.setString(columnNumberAccount++, account.getName());
                    statementAccount.setString(columnNumberAccount++, account.getPatronymic());
                    statementAccount.setString(columnNumberAccount++, account.getPhoneNumber());
                    statementAccount.setBoolean(columnNumberAccount++, account.isActive());
                    statementAccount.setInt(columnNumberAccount, account.getAccountRole().getId());

                    int countInsertRowsAccount = statementAccount.executeUpdate();
                    if (countInsertRowsAccount != 1) {
                        conn.rollback(savepoint);
                        errorMessage = "Insert into table Account has failed. We insert: " + countInsertRowsAccount + " rows for login: " + login;
                        logger.error(errorMessage);
                    } else {
                        if (account.getAccountRole().equals(AccountRole.CUSTOMER)) {
                            OrderDAO.getInstance().createCart(userId);
                        }
                        conn.commit();
                        resultAccountSet = true;
                        infoMessage = "Login: " + login + " CREATE COMPLETE!";
                        logger.info(infoMessage);
                    }
                } else {
                    conn.rollback(savepoint);
                }
            }
        } catch (SQLException e) {
            logger.error("SQL Exception in create account: ", e);
            try {
                conn.rollback(savepoint);
                assert savepoint != null;
                errorMessage = "Runtime Error when rollback to savepoint " + savepoint.getSavepointName();
                logger.error(errorMessage);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

// Восстановление по умолчанию
        try {
            conn.setAutoCommit(true);
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultAccountSet;
    }

    /**
     * The method changes account info in the database
     *
     * @param accountId  - account ID
     * @param surname    - account surname
     * @param name       - account name
     * @param patronymic - account password
     * @param phone      - account phone
     * @return - true if write to the database was successful
     */
    public boolean changeAccountData(int accountId, String surname, String name, String patronymic, String phone) {
        String errorMessage;
        boolean resultAccountSet = false;
        Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
        try (
                PreparedStatement statement = conn.prepareStatement(AccountSQL.UPDATE_ACCOUNT.getQuery())
        ) {
            int columnNumber = 1;
            statement.setString(columnNumber++, surname);
            statement.setString(columnNumber++, name);
            statement.setString(columnNumber++, patronymic);
            statement.setString(columnNumber++, phone);
            statement.setInt(columnNumber, accountId);

            int countUpdateRowsAccount = statement.executeUpdate();
            if (countUpdateRowsAccount != 1) {
                errorMessage = "Can't update data for account id = " + accountId;
                logger.error(errorMessage);
            } else {
                resultAccountSet = true;
            }
        } catch (SQLException e) {
            logger.trace("SQL exception in a method changeAccountData. ", e);
        }
        return resultAccountSet;
    }

    /**
     * The method finds account in the database by criteria
     *
     * @param query - sql query
     * @param val   - criteria
     * @return - Account instance if account was found or Account with ID = -1 if wasn't
     */
    private Account findBy(String query, Object... val) {
        Account foundAccount = new Account(-1);
        FindBy findBy = (Connection connection, String sql, Object... values) -> {
            PreparedStatement statement = connection.prepareStatement(sql);
            for (int i = 0; i < values.length; i++) {
                statement.setObject(i + 1, values[i]);
            }
            return statement;
        };
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = findBy.getPreparedStatement(conn, query, val);
                ResultSet rs = statement.executeQuery()
        ) {
            if (rs.next()) {
                foundAccount = createAccountFromDB(rs);
            }
        } catch (SQLException e) {
            logger.trace("SQL exception in a method findBy. ", e);
            e.printStackTrace();
        }
        return foundAccount;
    }

    /**
     * The method finds in the database customer by ID
     *
     * @param customerId - customer ID
     * @return - Customer instance if account was found or Customer with ID = -1 if wasn't
     */
    public Customer findCustomerById(int customerId) {
        return findCustomerBy(AccountSQL.FIND_ACCOUNT_BY_ID.getQuery(), customerId);
    }

    /**
     * The method finds customer in the database by login
     *
     * @param login - customer login
     * @return - Customer instance if account was found or Customer with ID = -1 if wasn't
     */
    public Customer findCustomerByLogin(String login) {
        return findCustomerBy(AccountSQL.FIND_ACCOUNT_BY_LOGIN.getQuery(), login);
    }

    /**
     * The method of getting a customer balance from the database
     *
     * @param accountId - customer ID
     * @return - customer balance
     */
    public int getCustomerBalance(int accountId) {
        int balance = 0;
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(AccountSQL.GET_BALANCE_BY_ID.getQuery())
        ) {
            statement.setInt(1, accountId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    balance = resultSet.getInt(ACCOUNT_BALANCE);
                }
            }
        } catch (SQLException e) {
            logger.trace("SQL exception in a method getCustomerBalance. ", e);
            e.printStackTrace();
        }
        return balance;
    }

    /**
     * The method finds customer in the database by criterion
     *
     * @param query     - sql query
     * @param parameter - criterion
     * @return - Customer instance if account was found or Customer with ID = -1 if wasn't
     */
    private Customer findCustomerBy(String query, Object parameter) {
        Customer foundCustomer = new Customer(-1);
        Account account;
        FindBy findBy = (Connection connection, String sql, Object... values) -> {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setObject(1, values[0]);
            return statement;
        };
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = findBy.getPreparedStatement(conn, query, parameter);
                ResultSet rs = statement.executeQuery()
        ) {
            if (rs.next()) {
                account = createAccountFromDB(rs);
                if (account.getAccountRole() != null && account.getAccountRole().equals(AccountRole.CUSTOMER)) {
                    foundCustomer = (Customer) account;
                }
            }
        } catch (SQLException e) {
            logger.trace("SQL exception in a method findCustomerBy. ", e);
            e.printStackTrace();
        }
        return foundCustomer;
    }

    /**
     * The method creates account instance from ResultSet
     *
     * @param rs - ResultSet
     * @return - Account instance if account was found or NULL if wasn't
     */
    private Account createAccountFromDB(ResultSet rs) {
        Account result = new Account(-1);
        try {
            Account.AccountBuilder accountBuilder = new Account.AccountBuilder(
                    rs.getString(ACCOUNT_SURNAME),
                    rs.getString(ACCOUNT_NAME),
                    AccountRole.valueOf(rs.getString(ACCOUNT_ROLE_NAME).toUpperCase()))
                    .withId(rs.getInt(ACCOUNT_ID))
                    .withPatronymic(rs.getString(ACCOUNT_PATRONYMIC))
                    .withPhoneNumber(rs.getString(ACCOUNT_PHONE))
                    .withStatus(rs.getBoolean(ACCOUNT_STATUS));
            if (accountBuilder.getAccountRole().equals(AccountRole.CUSTOMER)) {
                result = new Customer(accountBuilder, rs.getInt(ACCOUNT_BALANCE));
            } else {
                result = accountBuilder.build();
            }
        } catch (SQLException e) {
            logger.trace("SQL exception in a method createAccountFromDB. ", e);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * The method creates a Md5Password with personal and global salt
     *
     * @param userPassword - account password
     * @param userSalt     - account salt
     * @return - Md5Password
     */
    public static String getMd5Password(String userPassword, String userSalt) {
        String globalSalt = ApplicationConfiguration.INSTANCE.getGlobalSalt();
        String md5Password;
        if (userSalt == null) {
            String passwordWithSalt = globalSalt + userPassword + generateRandomSalt();
            md5Password = DigestUtils.md5Hex(passwordWithSalt);
        } else {
            String passwordWithSalt = globalSalt + userPassword + userSalt;
            md5Password = DigestUtils.md5Hex(passwordWithSalt);
        }
        return md5Password;
    }

    /**
     * The method generates personal random salt for password
     *
     * @return - random salt
     */
    private static String generateRandomSalt() {
        StringBuilder randomSalt = new StringBuilder();
        int maxLength = random.nextInt(5) + 5;
        for (int i = 0; i < maxLength; i++) {
            char c = (char) (random.nextInt(94) + 33);
            randomSalt.append(c);
        }
        return randomSalt.toString();
    }

    /**
     * The method finds account password and personal salt in the database by login
     *
     * @param login - account login
     * @return -  a map of password and personal salt
     */
    private static Map<String, String> getUserPasswordAndSaltFromDB(String login) {
        String infoMessage;
        Map<String, String> passwordAndSalt = new HashMap<>();
        String password;
        String salt;
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(AccountSQL.GET_PASSWORD_AND_SALT_BY_LOGIN.getQuery())
        ) {
            statement.setString(1, login);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    password = rs.getString(PASSWORD);
                    salt = rs.getString("salt");
                    passwordAndSalt.put(PASSWORD, password);
                    passwordAndSalt.put("salt", salt);
                }
            }
        } catch (
                SQLException e) {
            infoMessage = "SQL exception in a method getUserPasswordAndSaltFromDB for login: " + login;
            logger.trace(infoMessage, e);
        }

        return passwordAndSalt;
    }

    /**
     * The method updates account balance in the database
     *
     * @param customerId - customer ID
     * @param balance    - customer balance
     */
    public void increaseCustomerBalance(int customerId, int balance) {
        String loggerMessage;
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement psAccount = conn.prepareStatement(AccountSQL.UPDATE_BALANCE.getQuery())
        ) {
            psAccount.setInt(1, balance);
            psAccount.setInt(2, customerId);
            int updateRow = psAccount.executeUpdate();
            if (updateRow != 1) {
                loggerMessage = "Update in table Account has failed. We update: " + updateRow + " rows for accountId: " + customerId;
                logger.error(loggerMessage);
            } else {
                loggerMessage = "Update in table Account complete. For account id = " + customerId + " was set balance: " + balance;
                logger.info(loggerMessage);
            }
        } catch (SQLException e) {
            logger.trace("SQL exception in a method increaseCustomerBalance. ", e);
        }
    }

    /**
     * The method gets account salt
     *
     * @param login - account login
     * @return - account salt or NULL
     */
    public String getSaltByLogin(String login) {
        String salt = null;
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(AccountSQL.GET_SALT_BY_LOGIN.getQuery())
        ) {
            statement.setString(1, login);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    salt = rs.getString("salt");
                }
            }
        } catch (
                SQLException e) {
            logger.trace("SQL exception in a method getSaltByLogin. ", e);
        }
        return salt;
    }

    /**
     * The method updates account password in the database
     *
     * @param login    - customer login
     * @param password - customer new password
     * @return true if update was successful
     */
    public boolean setNewAccountPassword(String login, String password) {
        String loggerMessage;
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement ps = conn.prepareStatement(AccountSQL.UPDATE_ACCOUNT_PASSWORD.getQuery())
        ) {
            ps.setString(1, password);
            ps.setString(2, login);
            int updateRow = ps.executeUpdate();
            if (updateRow != 1) {
                loggerMessage = "Update in table Login has failed. We update: " + updateRow + " rows for accountLogin: " + login;
                logger.error(loggerMessage);
            } else {
                loggerMessage = "Update in table Login complete. For account login = " + login + " was set new password.";
                logger.info(loggerMessage);
                return true;
            }
        } catch (SQLException e) {
            logger.trace("SQL exception in a method setNewAccountPassword. ", e);
        }
        return false;
    }
}


