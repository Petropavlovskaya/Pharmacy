package by.petropavlovskaja.pharmacy.db;

import java.sql.Connection;

public interface IConnectionPool {
    Connection retrieveConnection();

    void releaseConnection(Connection connection);
}
