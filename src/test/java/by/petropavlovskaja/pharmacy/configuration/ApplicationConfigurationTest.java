package by.petropavlovskaja.pharmacy.configuration;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationConfigurationTest {

    @Test
    void getDbUrl() {
        assertEquals("jdbc:postgresql://localhost:5432/pharmacy", ApplicationConfiguration.INSTANCE.getDbUrl());
    }

    @Test
    void getDbUser() {
        assertEquals("olesia", ApplicationConfiguration.INSTANCE.getDbUser());
    }

    @Test
    void getInitPoolSize() {
        assertEquals(20, ApplicationConfiguration.INSTANCE.getInitPoolSize());
    }

    @Test
    void getMaxPoolSize() {
        assertEquals(100, ApplicationConfiguration.INSTANCE.getMaxPoolSize());
    }

    @Test
    void getPoolIncreaseStep() {
        assertEquals(5, ApplicationConfiguration.INSTANCE.getPoolIncreaseStep());
    }

    @Test
    void getGlobalSalt() {
        assertEquals(";Yw^1e", ApplicationConfiguration.INSTANCE.getGlobalSalt());
    }
}