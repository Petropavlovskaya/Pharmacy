package by.petropavlovskaja.pharmacy.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public enum ApplicationConfiguration {
    INSTANCE;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private int initPoolSize;
    private int maxPoolSize;
    private int poolIncreaseStep;
    private String globalSalt = "2";
//    private String globalSalt = ";Yw^1e";


    ApplicationConfiguration() {
        initProperties();
    }

    private void initProperties() {
        Logger logger = LoggerFactory.getLogger(ApplicationConfiguration.class);
        String propertyFile = getClass().getResource("/").getPath() + "application.properties";
        System.out.println("source file path = " + propertyFile);
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
                globalSalt = String.valueOf(properties.getProperty("poolIncreaseStep"));
            }
            logger.info("Properties loaded successful");
        } catch (IOException e) {
            logger.error("Properties has not been loaded", e);
            throw new Error("Properties has not been loaded", e);
        }
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public int getInitPoolSize() {
        return initPoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public int getPoolIncreaseStep() {
        return poolIncreaseStep;
    }

    public String getGlobalSalt() {
        return globalSalt;
    }

    @Override
    public String toString() {
        return "ApplicationConfiguration{" +
                "dbUrl='" + dbUrl + '\'' +
                ", dbUser='" + dbUser + '\'' +
                ", dbPassword='" + dbPassword + '\'' +
                ", initPoolSize=" + initPoolSize + '\'' +
                ", maxPoolSize=" + maxPoolSize + '\'' +
                ", poolIncreaseStep=" + poolIncreaseStep +
                '}';
    }
}
