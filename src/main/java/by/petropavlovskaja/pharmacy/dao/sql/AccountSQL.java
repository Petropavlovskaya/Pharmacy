package by.petropavlovskaja.pharmacy.dao.sql;

public enum AccountSQL {
    FIND_ACCOUNT_BY_ID("SELECT * FROM account a INNER JOIN role r on a.fk_role = r.id WHERE a.id = ?;"),
    FIND_ACCOUNT_BY_LOGIN("SELECT * FROM account a INNER JOIN \"role\" r on a.fk_role = r.id " +
            "INNER JOIN login l on a.id = l.id WHERE l.login = ?;"),
    FIND_LOGIN("SELECT login FROM login WHERE login = ?;"),
    GET_ALL_ACCOUNTS_LIST("SELECT * FROM account a INNER JOIN role r on a.fk_role = r.id;"),
    GET_ALL_CUSTOMERS("SELECT a.id, a.surname, a.\"name\", a.patronymic, a.phone FROM account a INNER JOIN role r " +
            "on a.fk_role = r.id WHERE r.role_name = 'Customer' AND status = true;"),
    GET_PASSWORD_AND_SALT_BY_LOGIN("SELECT * FROM login WHERE login = ?;"),
    GET_BALANCE_BY_ID("SELECT balance FROM account WHERE id = ?;"),
    INSERT_ACCOUNT("INSERT INTO account (id, surname, name, patronymic, phone, status, fk_role) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?);"),
    INSERT_LOGIN("INSERT INTO login (login, password, salt) VALUES (?, ?, ?);"),
    UPDATE_BALANCE("UPDATE account SET balance = ? WHERE id = ?"),
//    UPDATE_ACCOUNT("UPDATE account SET surname = ?, name = ?, patronymic = ?, phone_number = ? WHERE id = ?"),
//    DELETE_ACCOUNT("DELETE FROM account WHERE id = ?")
    ;

    private String query;

    AccountSQL(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
