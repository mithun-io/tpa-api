package com.tpa.config;

import org.kie.api.runtime.KieContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

/**
 * Test configuration that replaces the DroolsConfig KieContainer bean
 * to avoid JDK 25 compatibility issues (java.lang.Compiler was removed).
 * All @SpringBootTest classes that need the full application context will
 * use this no-op mock instead of the real Drools engine.
 */
@TestConfiguration
public class DroolsTestConfig {

    @Bean
    @Primary
    public KieContainer kieContainer() {
        return mock(KieContainer.class);
    }
}
