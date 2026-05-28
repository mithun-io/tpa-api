package com.tpa.support;

import com.tpa.entity.User;
import com.tpa.enums.Gender;
import com.tpa.enums.UserRole;
import com.tpa.enums.UserStatus;
import com.tpa.helper.AdminInitializer;
import com.tpa.helper.EnterpriseDataSeeder;
import com.tpa.repository.RefreshTokenRepository;
import com.tpa.repository.UserRepository;
import com.tpa.security.CustomUserDetails;
import com.tpa.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.kie.api.runtime.KieContainer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Abstract base for controller integration tests.
 * Provides a shared Spring context with MockMvc, JWT token generation,
 * and mocking of infrastructure beans (AdminInitializer, EnterpriseDataSeeder).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JwtUtil jwtUtil;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected RefreshTokenRepository refreshTokenRepository;

    @MockBean
    protected AdminInitializer adminInitializer;

    @MockBean
    protected EnterpriseDataSeeder enterpriseDataSeeder;

    @MockBean
    protected KieContainer kieContainer;

    protected String patientToken;
    protected String adminToken;
    protected String carrierToken;

    protected User patientUser;
    protected User adminUser;
    protected User carrierUser;

    @BeforeEach
    void baseSetUp() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();

        patientUser = userRepository.save(User.builder()
                .username("John Patient")
                .email("patient@test.com")
                .phoneNumber("+12025551001")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .address("123 Main St, New York, NY 10001")
                .password(passwordEncoder.encode("Patient@1234"))
                .gender(Gender.MALE)
                .userRole(UserRole.PATIENT)
                .userStatus(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build());

        adminUser = userRepository.save(User.builder()
                .username("Admin User")
                .email("admin@test.com")
                .phoneNumber("+12025550001")
                .dateOfBirth(LocalDate.of(1980, 1, 1))
                .address("Admin HQ")
                .password(passwordEncoder.encode("Admin@1234"))
                .gender(Gender.MALE)
                .userRole(UserRole.ADMIN)
                .userStatus(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build());

        carrierUser = userRepository.save(User.builder()
                .username("ABC Insurance Corp")
                .email("carrier@test.com")
                .phoneNumber("+12025552001")
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("100 Insurance Ave, Chicago, IL 60601")
                .password(passwordEncoder.encode("Carrier@1234"))
                .gender(null)
                .userRole(UserRole.CARRIER)
                .userStatus(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build());

        patientToken = generateToken(patientUser);
        adminToken = generateToken(adminUser);
        carrierToken = generateToken(carrierUser);
    }

    protected String generateToken(User user) {
        CustomUserDetails userDetails = new CustomUserDetails(user);
        return "Bearer " + jwtUtil.generateToken(userDetails);
    }
}
