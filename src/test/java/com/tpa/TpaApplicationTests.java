package com.tpa;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.tpa.helper.AdminInitializer;
import com.tpa.helper.EnterpriseDataSeeder;
import org.kie.api.runtime.KieContainer;

@SpringBootTest
@ActiveProfiles("test")
class TpaApplicationTests {

    @MockBean
    private AdminInitializer adminInitializer;

    @MockBean
    private EnterpriseDataSeeder enterpriseDemoDataSeeder;

    @MockBean
    private KieContainer kieContainer;

    @Test
    void contextLoads() {
    }

}
