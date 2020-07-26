package by.petropavlovskaja.pharmacy.dao.sql;

/**
 * Enumeration for account SQL query
 */
public enum AccountSQL {
    FIND_ACCOUNT_BY_ID("SELECT * FROM account a INNER JOIN role r on a.fk_role = r.id WHERE a.id = ?;"),
    FIND_ACCOUNT_BY_LOGIN("SELECT * FROM account a INNER JOIN \"role\" r on a.fk_role = r.id " +
            "INNER JOIN login l on a.id = l.id WHERE l.login = ?;"),
    FIND_LOGIN("SELECT login FROM login WHERE login = ?;"),
    GET_ALL_ACCOUNTS_LIST("SELECT * FROM account a INNER JOIN role r on a.fk_role = r.id;"),
    GET_ALL_CUSTOMERS("SELECT a.id, a.surname, a.\"name\", a.patronymic, a.phone FROM account a INNER JOIN role r " +
            "on a.fk_role = r.id WHERE r.role_name = 'Customer' AND status = true;"),
    GET_PASSWORD_AND_SALT_BY_LOGIN("SELECT * FROM login WHERE login = ?;"),
    GET_SALT_BY_LOGIN("SELECT salt FROM login WHERE login = ?;"),
    GET_BALANCE_BY_ID("SELECT balance FROM account WHERE id = ?;"),
    INSERT_ACCOUNT("INSERT INTO account (id, surname, name, patronymic, phone, status, fk_role) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?);"),
    INSERT_LOGIN("INSERT INTO login (login, password, salt) VALUES (?, ?, ?);"),
    UPDATE_BALANCE("UPDATE account SET balance = ? WHERE id = ?"),
    UPDATE_ACCOUNT("UPDATE account SET surname = ?, name = ?, patronymic = ?, phone = ? WHERE id = ?"),
    UPDATE_ACCOUNT_PASSWORD("UPDATE login SET password = ? WHERE login = ?"),
//    DELETE_ACCOUNT("DELETE FROM account WHERE id = ?")
    ;

    /**
     * Property - query
     */
    private String query;

    /**
     * Constructor - create account SQL query
     *
     * @param query - query
     */
    AccountSQL(String query) {
        this.query = query;
    }

    /**
     * The method of getting an account SQL query {@link AccountSQL#query}
     *
     * @return - SQL query
     */
    public String getQuery() {
        return query;
    }
}
