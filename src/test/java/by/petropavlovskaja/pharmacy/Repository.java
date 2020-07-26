package by.petropavlovskaja.pharmacy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public abstract class Repository<T> {
    public static Connection connection;

    public Repository() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:test");
    }

    public abstract void createTable() throws SQLException;

    public abstract void insertMembers() throws SQLException;
}
