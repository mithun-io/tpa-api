package com.tpa.service;

import com.tpa.dto.request.auth.*;
import com.tpa.dto.request.user.CarrierRequest;
import com.tpa.dto.request.user.PatientRequest;
import com.tpa.dto.response.auth.LoginResponse;
import com.tpa.dto.response.user.UserResponse;

import java.security.Principal;

public interface AuthService {

    void patientRegistration(PatientRequest patientRequest);

    void carrierRegistration(CarrierRequest carrierRequest);

    void verifyCarrierOtp(OtpRequest otpRequest);

    void verifyPatientOtp(OtpRequest otpRequest);

    void resendOtp(String email);

    LoginResponse login(LoginRequest loginRequest);

    void logout(String username);

    UserResponse passwordChange(PasswordChangeRequest passwordChangeRequest, Principal principal);

    void forgetPassword(String email);

    void passwordReset(PasswordResetRequest passwordResetRequest);

    LoginResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}