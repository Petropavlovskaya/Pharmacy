package by.petropavlovskaja.pharmacy.db.impl;

import by.petropavlovskaja.pharmacy.db.IConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import static by.petropavlovskaja.pharmacy.configuration.ApplicationConfiguration.INSTANCE;

public enum ConnectionPool implements IConnectionPool {
    ConnectionPool;

    // не забыть сделать коллекции приватными
    public final BlockingQueue<Connection> availableConnections = new LinkedBlockingDeque<>();
    public final BlockingQueue<Connection> takenConnections = new LinkedBlockingDeque<>();

    private static Logger logger = LoggerFactory.getLogger(ConnectionPool.class);

    ConnectionPool() {
        addConnections(INSTANCE.getInitPoolSize());
    }

    private void addConnections(int size) {
        logger.info("It will initialize " + size + " real connections.");
        for (int i = 0; i < size; i++) {
            try {
                Class.forName("org.postgresql.Driver");
                Connection$Proxy connection = new Connection$Proxy(DriverManager.getConnection(INSTANCE.getDbUrl(),
                        INSTANCE.getDbUser(), INSTANCE.getDbPassword()));
                availableConnections.add(connection);
            } catch (SQLException e) {
                logger.error("We can't init connection # " + i + ", because:" + e);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Connection retrieveConnection() {
        Connection connection = null;
        if (availableConnections.size() != 0) {
            try {
                connection = availableConnections.take();
//                logger.info("Available connection is retrieved (get)");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (availableConnections.size() + takenConnections.size() < INSTANCE.getMaxPoolSize()) {
            logger.info("Available connections are finished. Add " + INSTANCE.getPoolIncreaseStep() + " connections");
            addConnections(INSTANCE.getPoolIncreaseStep());
        }
        if (Objects.nonNull(connection)) {
            availableConnections.remove(connection);
            takenConnections.add(connection);
        }
        return connection;
    }

    @Override
    public void releaseConnection(Connection connection) {
        try {
            takenConnections.remove(connection);
            availableConnections.add(connection);
//            logger.info("Available connection is releasing (put)");
        } catch (Exception e) {
            logger.error("Something went wrong with releasing connection", e);
        }
    }

    public void closeRealConnection() {
        int count = 0;
        Connection$Proxy connectionProxy;
        while (availableConnections.size() > 0) {
            try {
                connectionProxy = (Connection$Proxy) availableConnections.poll();
                connectionProxy.realClose();
                count++;
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        if (count != INSTANCE.getInitPoolSize()) {
            System.out.println("We closed " + count + " connections.");
        } else
            System.out.println("We closed all connections (" + count + ").");
    }
}
