package by.petropavlovskaja.pharmacy.configuration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationConfigurationTest {

    @Test
    void getDbUrl_returnValidDatabaseUrl() {
        assertEquals("jdbc:postgresql://localhost:5432/pharmacy", ApplicationConfiguration.INSTANCE.getDbUrl());
    }

    @Test
    void getDbUser_returnValidDatabaseUser() {
        assertEquals("olesia", ApplicationConfiguration.INSTANCE.getDbUser());
    }

    @Test
    void getInitPoolSize_returnValidInitValueOfPoolSize() {
        assertEquals(20, ApplicationConfiguration.INSTANCE.getInitPoolSize());
    }

    @Test
    void getMaxPoolSize_returnValidMaxValueOfPoolSize() {
        assertEquals(100, ApplicationConfiguration.INSTANCE.getMaxPoolSize());
    }

    @Test
    void getPoolIncreaseStep_returnValidIncreaseStepValueOfPoolSize() {
        assertEquals(5, ApplicationConfiguration.INSTANCE.getPoolIncreaseStep());
    }

    @Test
    void getGlobalSalt_returnValidValueOfGlobalSaltForUsersPasswords() {
        assertEquals(";Yw^1e", ApplicationConfiguration.INSTANCE.getGlobalSalt());
    }
}