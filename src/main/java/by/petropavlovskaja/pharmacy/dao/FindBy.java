package by.petropavlovskaja.pharmacy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Functional interface for create prepared statement of a variable number of parameters
 */
interface FindBy {

    /**
     * @param conn   - Connection to database
     * @param sql    - sql query
     * @param values - parameters for sql query
     * @return - prepared statement of a variable number of parameters
     * @throws SQLException
     */
    PreparedStatement getPreparedStatement(Connection conn, String sql, Object... values) throws SQLException;
}
