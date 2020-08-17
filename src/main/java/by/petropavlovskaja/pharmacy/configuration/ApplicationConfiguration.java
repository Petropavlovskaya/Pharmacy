package by.petropavlovskaja.pharmacy.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * Enumeration for configuration the application. Has property <b>INSTANCE</b>
 */
public enum ApplicationConfiguration {
    /**
     * Property - instance
     */
    INSTANCE;
    /**
     * Property - url
     */
    private String dbUrl;
    /**
     * Property - user
     */
    private String dbUser;
    /**
     * Property - password
     */
    private String dbPassword;
    /**
     * Property - starting pool size
     */
    private int initPoolSize;
    /**
     * Property - maximum pool size
     */
    private int maxPoolSize;
    /**
     * Property - pool increase step
     */
    private int poolIncreaseStep;
    /**
     * Property - global salt
     */
    private String globalSalt;

    /**
     * Constructor - create INSTANCE of class
     */
    ApplicationConfiguration() {
        initProperties();
    }

    /**
     * The method reads and initializing the properties from a resources file for application database connect
     */
    private void initProperties() {
        Logger logger = LoggerFactory.getLogger(ApplicationConfiguration.class);
        String propertyFile = getClass().getResource("/").getPath() + "application.properties";
        try (InputStream inputStream = new FileInputStream(propertyFile)) {
            Properties properties = new Properties();
            properties.load(inputStream);
            dbUrl = properties.getProperty("jdbc.url");
            dbUser = properties.getProperty("jdbc.user");
            dbPassword = properties.getProperty("jdbc.password");
            if (Objects.nonNull(properties.getProperty("initPoolSize"))) {
                initPoolSize = Integer.parseInt(properties.getProperty("initPoolSize"));
            }
            if (Objects.nonNull(properties.getProperty("maxPoolSize"))) {
                maxPoolSize = Integer.parseInt(properties.getProperty("maxPoolSize"));
            }
            if (Objects.nonNull(properties.getProperty("poolIncreaseStep"))) {
                poolIncreaseStep = Integer.parseInt(properties.getProperty("poolIncreaseStep"));
            }
            if (Objects.nonNull(properties.getProperty("passwordGlobalSalt"))) {
                globalSalt = String.valueOf(properties.getProperty("passwordGlobalSalt"));
            }
            logger.info("Properties loaded successful");
        } catch (IOException e) {
            logger.error("Properties have not been loaded.", e);
        }
    }

    /**
     * Method for get property of {@link ApplicationConfiguration#dbUrl}
     *
     * @return - Url to connect to the database
     */
    public String getDbUrl() {
        return dbUrl;
    }

    /**
     * Method for get property of {@link ApplicationConfiguration#dbUser}
     *
     * @return - User login to connect to the database
     */
    public String getDbUser() {
        return dbUser;
    }

    /**
     * Method for get property of {@link ApplicationConfiguration#dbPassword}
     *
     * @return - Password to connect to the database
     */
    public String getDbPassword() {
        return dbPassword;
    }

    /**
     * Method for get property of {@link ApplicationConfiguration#initPoolSize}
     *
     * @return - Starting size of connection pool to the database
     */
    public int getInitPoolSize() {
        return initPoolSize;
    }

    /**
     * Method for get property of {@link ApplicationConfiguration#maxPoolSize}
     *
     * @return - Maximum size of connection pool to the database
     */
    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    /**
     * Method for get property of {@link ApplicationConfiguration#poolIncreaseStep}
     *
     * @return - Step for increase size of connection pool to the database
     */
    public int getPoolIncreaseStep() {
        return poolIncreaseStep;
    }

    /**
     * Method for get property of {@link ApplicationConfiguration#globalSalt}
     *
     * @return - Global salt for protect account passwords
     */
    public String getGlobalSalt() {
        return globalSalt;
    }

    @Override
    public String toString() {
        return "ApplicationConfiguration{" +
                "dbUrl='" + dbUrl + '\'' +
                ", dbUser='" + dbUser + '\'' +
                ", initPoolSize=" + initPoolSize + '\'' +
                ", maxPoolSize=" + maxPoolSize + '\'' +
                ", poolIncreaseStep=" + poolIncreaseStep +
                '}';
    }
}
