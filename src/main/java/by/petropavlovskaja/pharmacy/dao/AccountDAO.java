package by.petropavlovskaja.pharmacy.dao;

import by.petropavlovskaja.pharmacy.configuration.ApplicationConfiguration;
import by.petropavlovskaja.pharmacy.dao.sql.AccountSQL;
import by.petropavlovskaja.pharmacy.db.impl.ConnectionPool;
import by.petropavlovskaja.pharmacy.model.account.Account;
import by.petropavlovskaja.pharmacy.model.account.AccountRole;
import by.petropavlovskaja.pharmacy.model.account.Customer;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public final class AccountDAO {
    private static Logger logger = LoggerFactory.getLogger(AccountDAO.class);

    private AccountDAO() {
    }

    private static class AccountDAOHolder {
        public static final AccountDAO ACCOUNT_DAO = new AccountDAO();
    }

    public static AccountDAO getInstance() {
        return AccountDAOHolder.ACCOUNT_DAO;
    }


    public Account find(int id) {
        return findBy(AccountSQL.FIND_ACCOUNT_BY_ID.getQuery(), id);
    }

    public Account checkLoginAndPassword(String login, String password) {
        Account checkedAccount = new Account(-1);
        Map<String, String> passwordAndSalt = getUserPasswordAndSaltFromDB(login);

        if (!passwordAndSalt.isEmpty()) {
            String dbPassword = passwordAndSalt.get("password");
            String dbSalt = passwordAndSalt.get("salt");
            String md5Password = getMd5Password(password, dbSalt);
            logger.info("bdPassword = " + dbPassword + " and dbSalt = " + dbSalt + " md5Passw = " + md5Password);

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

    public boolean isLoginBusy(String login) {
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(AccountSQL.FIND_LOGIN.getQuery())
        ) {
            statement.setString(1, login);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (
                SQLException e) {
            System.out.println("Nothing was find ((");
            e.printStackTrace();
        }
        return false;
    }

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
                        rs.getString("surname"),
                        rs.getString("name"),
                        AccountRole.CUSTOMER)
                        .withId(rs.getInt("id"))
                        .withPatronymic(rs.getString("patronymic"))
                        .withPhoneNumber(rs.getString("phone"));
                accountSet.add(accountBuilder.build());
            }

        } catch (
                SQLException e) {
            System.out.println("Nothing was find ((");
            e.printStackTrace();
        }
        return accountSet;
    }

    public boolean create(Account account, String login, String password) {
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
            savepoint = conn.setSavepoint("start");
            System.out.println("Account autocommit false, savepoint");

// Create login & password
            String salt = generateRandomSalt();
            statementLogin.setString(1, login);
            statementLogin.setString(2, getMd5Password(password, salt));
            statementLogin.setString(3, salt);
            countInsertRowsLogin = statementLogin.executeUpdate();
            if (countInsertRowsLogin != 1) {
                conn.rollback(savepoint);
                logger.error("Insert into table Login is failed. We insert: " + countInsertRowsLogin + " rows for login: " + login);
            } else {
                ResultSet resultSet = statementLogin.getGeneratedKeys();
                if (resultSet.next()) {
                    userId = resultSet.getInt(1);
                    logger.info("Insert into table Login complete. Login " + login + " is added. UserId is: " + userId);

// Crete account data
                    statementAccount.setInt(1, userId);
                    statementAccount.setString(2, account.getSurname());
                    statementAccount.setString(3, account.getName());
                    statementAccount.setString(4, account.getPatronymic());
                    statementAccount.setString(5, account.getPhoneNumber());
                    statementAccount.setBoolean(6, account.isActive());
                    statementAccount.setInt(7, account.getAccountRole().getId());

                    int countInsertRowsAccount = statementAccount.executeUpdate();
                    if (countInsertRowsAccount != 1) {
                        conn.rollback(savepoint);
                        logger.error("Insert into table Account is failed. We insert: " + countInsertRowsAccount + " rows for login: " + login);
                    } else {
                    /*System.out.println("Account commit");
                    conn.commit();*/
                        if (account.getAccountRole().equals(AccountRole.CUSTOMER)) {
                            OrderDAO.getInstance().createCart(userId);
                        }
                        conn.commit();
                        resultAccountSet = true;
                        logger.info("Login: " + login + "CREATE COMPLETE!");
                    }
                } else {
                    conn.rollback(savepoint);
                }
            }
        } catch (SQLException e) {
            logger.error("SQL Exception in create account: " + e);
            try {
                conn.rollback(savepoint);
                System.out.println("Login rollback savepoint Exception");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

// Восстановление по умолчанию
        try {
            conn.setAutoCommit(true);
            System.out.println("Account autocommit true");
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultAccountSet;
    }

    private Account findBy(String sql, Object... values) {
        Account foundAccount = new Account(-1);
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = prepareStatement(conn, sql, values);
                ResultSet rs = statement.executeQuery()
        ) {
            if (rs.next()) {
                foundAccount = createAccountFromDB(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foundAccount;
    }

    public Customer findCustomerById(int customerId) {
        return findCustomerBy(AccountSQL.FIND_ACCOUNT_BY_ID.getQuery(), customerId);
    }

    public Customer findCustomerByLogin(String login) {
        return findCustomerBy(AccountSQL.FIND_ACCOUNT_BY_LOGIN.getQuery(), login);
    }

    public int getCustomerBalance(int accountId) {
        int balance = 0;
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(AccountSQL.GET_BALANCE_BY_ID.getQuery())
        ) {
            statement.setInt(1, accountId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                balance = resultSet.getInt("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return balance;
    }

    private Customer findCustomerBy(String query, Object parameter) {
        Customer foundCustomer = new Customer(-1);
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = prepareStatement(conn, query, parameter);
                ResultSet rs = statement.executeQuery()
        ) {
            if (rs.next()) {
                foundCustomer = (Customer) createAccountFromDB(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foundCustomer;
    }

    private Account createAccountFromDB(ResultSet rs) {
        Account result = null;
        try {
            Account.AccountBuilder accountBuilder = new Account.AccountBuilder(
                    rs.getString("surname"),
                    rs.getString("name"),
                    AccountRole.valueOf(rs.getString("role_name").toUpperCase()))
                    .withId(rs.getInt("id"))
                    .withPatronymic(rs.getString("patronymic"))
                    .withPhoneNumber(rs.getString("phone"))
                    .withStatus(rs.getBoolean("status"));
            if (accountBuilder.getAccountRole().equals(AccountRole.CUSTOMER)) {
                result = new Customer(accountBuilder, rs.getInt("balance"), rs.getInt("credit"));
            } else {
                result = accountBuilder.build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static PreparedStatement prepareStatement(Connection conn, String sql, Object... values) throws
            SQLException {
        PreparedStatement statement = conn.prepareStatement(sql);
        for (int i = 0; i < values.length; i++) {
            statement.setObject(i + 1, values[i]);
        }
        return statement;
    }

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

    private static String generateRandomSalt() {
        Random r = new Random();
        StringBuilder randomSalt = new StringBuilder();
        int maxLength = r.nextInt(5) + 5;
        for (int i = 0; i < maxLength; i++) {
            char c = (char) (r.nextInt(94) + 33);
            randomSalt.append(c);
        }
        return randomSalt.toString();
    }

    private static Map<String, String> getUserPasswordAndSaltFromDB(String login) {
        Map<String, String> passwordAndSalt = new HashMap<>();
        String password;
        String salt;
        try (
                Connection conn = ConnectionPool.ConnectionPool.retrieveConnection();
                PreparedStatement statement = conn.prepareStatement(AccountSQL.GET_PASSWORD_AND_SALT_BY_LOGIN.getQuery())
        ) {
            statement.setString(1, login);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                password = rs.getString("password");
                salt = rs.getString("salt");
                passwordAndSalt.put("password", password);
                passwordAndSalt.put("salt", salt);
            }
        } catch (
                SQLException e) {
            logger.info("Password and salt wasn't find for login: " + login);
            e.printStackTrace();
        }

        return passwordAndSalt;
    }
}


