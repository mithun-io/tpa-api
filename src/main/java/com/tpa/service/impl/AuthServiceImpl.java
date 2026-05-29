package com.tpa.service.impl;

import com.tpa.dto.request.auth.*;
import com.tpa.dto.request.user.CarrierRequest;
import com.tpa.dto.request.user.PatientRequest;
import com.tpa.dto.response.auth.LoginResponse;
import com.tpa.dto.response.user.UserResponse;
import com.tpa.entity.Patient;
import com.tpa.entity.User;
import com.tpa.enums.UserRole;
import com.tpa.enums.UserStatus;
import com.tpa.exception.BadRequestException;
import com.tpa.exception.ConflictException;
import com.tpa.exception.NoResourceFoundException;
import com.tpa.security.CustomUserDetails;
import com.tpa.security.CustomUserDetailsService;
import com.tpa.helper.EmailService;
import com.tpa.helper.RedisService;
import com.tpa.mapper.UserMapper;
import com.tpa.repository.PatientRepository;
import com.tpa.repository.UserRepository;
import com.tpa.security.JwtUtil;
import com.tpa.service.AuthService;
import com.tpa.service.RefreshTokenService;
import com.tpa.entity.RefreshToken;
import com.tpa.repository.RefreshTokenRepository;
import com.tpa.entity.Carrier;
import com.tpa.repository.CarrierRepository;
import com.tpa.kafka.producer.ClaimEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.security.Principal;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final CarrierRepository carrierRepository;

    private final UserMapper userMapper;

    private final CustomUserDetailsService customUserDetailsService;
    private final RedisService redisService;
    private final EmailService emailService;

    private final JwtUtil jwtUtil;
    private final SecureRandom secureRandom;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ClaimEventProducer claimEventProducer;

    private Integer generateOtp() {
        return secureRandom.nextInt(100000, 1000000);
    }

    @Transactional
    @Override
    public void patientRegistration(PatientRequest patientRequest) {
        if (userRepository.existsByEmailAndPhoneNumber(patientRequest.getEmail(), patientRequest.getPhoneNumber())) {
            throw new ConflictException("User already exists");
        }
        if (redisService.isPendingPatientExists(patientRequest.getEmail())) {
            throw new ConflictException("Pending registration already exists");
        }
        if (redisService.isPendingCarrierExists(patientRequest.getEmail())) {
            throw new ConflictException("A carrier registration is already pending for this email");
        }

        int otp = generateOtp();
        emailService.sendPatientRegistrationOtp(patientRequest.getName(), patientRequest.getEmail(), otp);
        redisService.storePatientOtp(patientRequest.getEmail(), otp);
        redisService.storePendingPatient(patientRequest.getEmail(), patientRequest);
    }

    @Transactional
    @Override
    public void verifyPatientOtp(OtpRequest otpRequest) {
        Integer storedOtp = redisService.getPatientOtp(otpRequest.getEmail());
        PatientRequest storedPatient = redisService.getPendingPatient(otpRequest.getEmail());

        if (storedOtp == null) {
            throw new BadRequestException("Otp expired or invalid");
        }
        if (redisService.getOtpAttempts(otpRequest.getEmail()) >= 5) {
            throw new BadRequestException("Max OTP attempts reached. Please request a new OTP.");
        }
        if (storedPatient == null) {
            throw new BadRequestException("No pending registration found");
        }
        if (!String.valueOf(storedOtp).equals(otpRequest.getOtp())) {
            redisService.incrementOtpAttempt(otpRequest.getEmail());
            throw new BadRequestException("Invalid otp");
        }
        if (userRepository.existsByEmail(storedPatient.getEmail())) {
            throw new ConflictException("User already exists!");
        }

        User user = User.builder()
                .username(storedPatient.getName())
                .email(storedPatient.getEmail())
                .phoneNumber(storedPatient.getPhoneNumber())
                .dateOfBirth(storedPatient.getDateOfBirth())
                .address(storedPatient.getAddress())
                .password(passwordEncoder.encode(storedPatient.getPassword()))
                .gender(storedPatient.getGender())
                .userRole(UserRole.PATIENT)
                .userStatus(UserStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
        user = userRepository.save(user);

        Patient patient = Patient.builder()
                .user(user)
                .build();
        patientRepository.save(patient);

        final String username = user.getUsername();
        final String email = user.getEmail();

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    emailService.sendPatientRegistrationConfirmation(username, email);
                } catch (Exception e) {
                    log.error("Failed to send patient registration confirmation email to {}: {}", email, e.getMessage());
                }
            }
        });

        redisService.deletePatientOtp(otpRequest.getEmail());
        redisService.deletePendingPatient(otpRequest.getEmail());
        redisService.deleteOtpAttempt(otpRequest.getEmail());
    }

    @Transactional
    @Override
    public void carrierRegistration(CarrierRequest carrierRequest) {
        if (userRepository.existsByEmail(carrierRequest.getEmail())) {
            throw new ConflictException("User already exists");
        }
        if (carrierRepository.existsByRegistrationNumber(carrierRequest.getRegistrationNumber())) {
            throw new ConflictException("A carrier with this registration number already exists");
        }
        if (redisService.isPendingCarrierExists(carrierRequest.getEmail())) {
            throw new ConflictException("Pending carrier registration already exists");
        }
        if (redisService.isPendingPatientExists(carrierRequest.getEmail())) {
            throw new ConflictException("A patient registration is already pending for this email");
        }

        int otp = generateOtp();
        emailService.sendCarrierRegistrationOtp(carrierRequest.getCompanyName(), carrierRequest.getEmail(), otp);
        redisService.storeCarrierOtp(carrierRequest.getEmail(), otp);
        redisService.storePendingCarrier(carrierRequest.getEmail(), carrierRequest);
    }

    @Transactional
    @Override
    public void verifyCarrierOtp(OtpRequest otpRequest) {
        Integer storedOtp = redisService.getCarrierOtp(otpRequest.getEmail());
        CarrierRequest stored = redisService.getPendingCarrier(otpRequest.getEmail());

        if (storedOtp == null) {
            throw new BadRequestException("Otp expired or invalid");
        }
        if (redisService.getOtpAttempts(otpRequest.getEmail()) >= 5) {
            throw new BadRequestException("Max OTP attempts reached. Please request a new OTP.");
        }
        if (stored == null) {
            throw new BadRequestException("No pending carrier registration found");
        }
        if (!String.valueOf(storedOtp).equals(otpRequest.getOtp())) {
            redisService.incrementOtpAttempt(otpRequest.getEmail());
            throw new BadRequestException("Invalid otp");
        }
        if (userRepository.existsByEmail(stored.getEmail())) {
            throw new ConflictException("User already exists");
        }
        if (carrierRepository.existsByRegistrationNumber(stored.getRegistrationNumber())) {
            throw new ConflictException("A carrier with registration number '" + stored.getRegistrationNumber() + "' already exists");
        }

        User user = User.builder()
                .username(stored.getCompanyName())
                .email(stored.getEmail())
                .phoneNumber(stored.getPhoneNumber())
                .dateOfBirth(java.time.LocalDate.now())
                .address(stored.getAddress())
                .password(passwordEncoder.encode(stored.getPassword()))
                .gender(null)
                .userRole(UserRole.CARRIER)
                .userStatus(UserStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        user = userRepository.save(user);

        Carrier carrier = Carrier.builder()
                .user(user)
                .companyName(stored.getCompanyName())
                .registrationNumber(stored.getRegistrationNumber())
                .companyType(stored.getCompanyType())
                .licenseNumber(stored.getLicenseNumber())
                .taxId(stored.getTaxId())
                .website(stored.getWebsite())
                .build();
        carrier = carrierRepository.save(carrier);

        redisService.deleteCarrierOtp(otpRequest.getEmail());
        redisService.deletePendingCarrier(otpRequest.getEmail());
        redisService.deleteOtpAttempt(otpRequest.getEmail());

        final Long carrierId = carrier.getId();
        final String companyName = carrier.getCompanyName();
        final String email = user.getEmail();
        final Carrier carrierRef = carrier;
        final String username = user.getUsername();

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    emailService.sendCarrierRegistrationConfirmation(username, email);
                } catch (Exception e) {
                    log.error("Failed to send carrier registration confirmation email to {}: {}", email, e.getMessage());
                }
                log.info("[POST-COMMIT] Running Kafka publishing for carrier {}", carrierId);
                try {
                    claimEventProducer.publishCarrierCreatedEvent(carrierId, companyName, email);
                } catch (Exception ex) {
                    log.warn("Post-commit Kafka publish failed for carrier {}: {}", carrierId, ex.getMessage());
                }
            }
        });

        log.info("VerifyCarrierOtp completed synchronously. Email and Kafka deferred to post-commit.");
    }


    @Override
    public void resendOtp(String email) {
        resendPatientOtp(email);
    }

    @Override
    public void resendPatientOtp(String email) {
        PatientRequest patient = redisService.getPendingPatient(email);

        if (patient == null) {
            throw new BadRequestException("No pending patient registration found");
        }

        redisService.deletePatientOtp(email);

        int otp = generateOtp();
        emailService.sendPatientRegistrationOtp(patient.getName(), patient.getEmail(), otp);
        redisService.storePatientOtp(patient.getEmail(), otp);
    }

    @Override
    public void resendCarrierOtp(String email) {
        CarrierRequest carrier = redisService.getPendingCarrier(email);

        if (carrier == null) {
            throw new BadRequestException("No pending carrier registration found");
        }

        redisService.deleteCarrierOtp(email);

        int otp = generateOtp();
        emailService.sendCarrierRegistrationOtp(carrier.getCompanyName(), carrier.getEmail(), otp);
        redisService.storeCarrierOtp(carrier.getEmail(), otp);
    }

    @Transactional
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new NoResourceFoundException("user not found"));

        if (user.getUserStatus() != UserStatus.ACTIVE) {
            if (user.getUserRole() == UserRole.CARRIER && (user.getUserStatus() == UserStatus.PENDING || user.getUserStatus() == UserStatus.INACTIVE)) {
                throw new BadRequestException("Your carrier account is pending admin approval. You will be notified once approved.");
            }
            if (user.getUserRole() == UserRole.CARRIER && user.getUserStatus() == UserStatus.BLOCKED) {
                throw new BadRequestException("Your carrier account has been rejected. Please contact support.");
            }
            throw new BadRequestException("account is not active. current status: " + user.getUserStatus().name().toLowerCase());
        }

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(loginRequest.getEmail());

        String token = jwtUtil.generateToken(customUserDetails);
        log.info("{} Logged in successfully", user.getUsername());

        UserResponse userResponse = userMapper.toUserResponse(user);
        
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());
        
        return new LoginResponse(token, refreshToken.getRawToken(), userResponse);
    }

    @Transactional
    @Override
    public void logout(String username) {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new NoResourceFoundException("User not found"));

        refreshTokenService.deleteByUserId(user.getId());
        log.info("User logged out successfully: {}", username);
    }

    @Transactional
    @Override
    public UserResponse passwordChange(PasswordChangeRequest passwordChangeRequest, Principal principal) {
        String email = principal.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NoResourceFoundException("User not found"));

        if (!passwordEncoder.matches(passwordChangeRequest.getPreviousPassword(), user.getPassword())) {
            throw new BadRequestException("Password is incorrect");
        }

        if (passwordEncoder.matches(passwordChangeRequest.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("New password cannot be same as previous password");
        }

        user.setPassword(passwordEncoder.encode(passwordChangeRequest.getNewPassword()));
        userRepository.save(user);
        
        // Revoke all existing sessions/tokens
        refreshTokenService.deleteByUserId(user.getId());
        
        return userMapper.toUserResponse(user);
    }

    @Override
    public void forgetPassword(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NoResourceFoundException("User not found"));

        int otp = generateOtp();
        emailService.sendOtp(user.getUsername(), email, otp);
        redisService.storeOtp(email, otp);
    }

    @Transactional
    @Override
    public void passwordReset(PasswordResetRequest passwordResetRequest) {
        User user = userRepository.findByEmail(passwordResetRequest.getEmail()).orElseThrow(() -> new NoResourceFoundException("User not found"));

        Integer storedOtp = redisService.getOtp(passwordResetRequest.getEmail());
        if (storedOtp == null) {
            throw new BadRequestException("Otp expired or invalid");
        }
        if (redisService.getOtpAttempts(passwordResetRequest.getEmail()) >= 5) {
            throw new BadRequestException("Max OTP attempts reached. Please request a new OTP.");
        }
        if (!String.valueOf(storedOtp).equals(passwordResetRequest.getOtp())) {
            redisService.incrementOtpAttempt(passwordResetRequest.getEmail());
            throw new BadRequestException("Invalid otp");
        }
        if (passwordEncoder.matches(passwordResetRequest.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("New password cannot be same as previous password");
        }

        user.setPassword(passwordEncoder.encode(passwordResetRequest.getNewPassword()));
        userRepository.save(user);
        
        // Revoke all existing sessions/tokens
        refreshTokenService.deleteByUserId(user.getId());

        final String username = user.getUsername();
        final String finalEmail = user.getEmail();

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    emailService.sendConfirmation(username, finalEmail);
                } catch (Exception e) {
                    log.error("Failed to send password reset confirmation email to {}: {}", finalEmail, e.getMessage());
                }
            }
        });
        
        redisService.deleteOtp(passwordResetRequest.getEmail());
        redisService.deleteOtpAttempt(passwordResetRequest.getEmail());
    }

    @Transactional
    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String hashedToken = hashToken(request.getRefreshToken());
        RefreshToken refreshToken = refreshTokenRepository.findByToken(hashedToken).orElseThrow(() -> new BadRequestException("Invalid refresh token"));
        refreshTokenService.verifyExpiration(refreshToken);

        User user = refreshToken.getUser();

        CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(user.getEmail());

        String accessToken = jwtUtil.generateToken(customUserDetails);

        UserResponse userResponse = userMapper.toUserResponse(user);

        return LoginResponse.builder()
                .token(accessToken)
                .refreshToken(request.getRefreshToken())
                .userResponse(userResponse)
                .build();
    }

    private String hashToken(String token) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash token", e);
        }
    }
}
