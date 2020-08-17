package by.petropavlovskaja.pharmacy.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static by.petropavlovskaja.pharmacy.configuration.ApplicationConfiguration.INSTANCE;

/**
 * Enumeration for the database connection pool. Has property <b>ConnectionPool</b>,
 * <b>availableConnections</b> and <b>takenConnections</b>
 */
public enum ConnectionPool {

    /**
     * Property - connection pool instance
     */
    ConnectionPool;

    /**
     * Property - the available database connections
     */
    private final BlockingQueue<Connection> availableConnections = new LinkedBlockingDeque<>();

    /**
     * Property - the taken database connections
     */
    private final BlockingQueue<Connection> takenConnections = new LinkedBlockingDeque<>();

    private static Logger logger = LoggerFactory.getLogger(ConnectionPool.class);

    /**
     * Constructor - create INSTANCE of the class
     */
    ConnectionPool() {
        addConnections(INSTANCE.getInitPoolSize());
    }

    /**
     * The method adds connections to connection pool
     *
     * @param size - size of the connections
     */
    private void addConnections(int size) {
        for (int i = 0; i < size; i++) {
            try {
                Class.forName("org.postgresql.Driver");
                Connection$Proxy connection = new Connection$Proxy(DriverManager.getConnection(INSTANCE.getDbUrl(),
                        INSTANCE.getDbUser(), INSTANCE.getDbPassword()));
                availableConnections.add(connection);
            } catch (SQLException e) {
                logger.trace("SQL exception in method addConnections. ", e);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The method of getting a connection from the connection pool and adds connections if they are finished
     *
     * @return connection
     */
    public Connection retrieveConnection() {
        Connection connection = null;
        if (!availableConnections.isEmpty()) {
            try {
                connection = availableConnections.take();
            } catch (InterruptedException e) {
                logger.trace("Exception in a method retrieveConnection. ", e);
            }
        } else if (availableConnections.size() + takenConnections.size() < INSTANCE.getMaxPoolSize()) {
            logger.info("Available connections are finished. New connections will be added.");
            addConnections(INSTANCE.getPoolIncreaseStep());
        }
        if (Objects.nonNull(connection)) {
            availableConnections.remove(connection);
            takenConnections.add(connection);
        }
        return connection;
    }

    /**
     * The method releases a connection into the connection pool
     *
     * @param connection - connection
     */
    public void releaseConnection(Connection connection) {
        try {
            takenConnections.remove(connection);
            availableConnections.add(connection);
        } catch (Exception e) {
            logger.error("Something went wrong with releasing connection", e);
        }
    }

    /**
     * The method closes all the real connection in the connection pool
     */
    public void closeRealConnection() {
        int count = 0;
        Connection$Proxy connectionProxy;
        while (!availableConnections.isEmpty()) {
            try {
                connectionProxy = (Connection$Proxy) availableConnections.poll();
                connectionProxy.realClose();
                count++;
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        if (count != INSTANCE.getInitPoolSize()) {
            logger.trace("There wasn't close all connections.");
        } else
            logger.trace("There was close all connections.");
    }
}
