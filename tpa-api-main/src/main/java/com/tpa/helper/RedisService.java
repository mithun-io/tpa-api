package com.tpa.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpa.dto.request.user.CarrierRequest;
import com.tpa.dto.request.user.PatientRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@Component
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String OTP_PREFIX = "OTP:";
    private static final String PATIENT_OTP_PREFIX = "OTP:PATIENT:";
    private static final String CARRIER_OTP_PREFIX = "OTP:CARRIER:";
    private static final String PENDING_PATIENT_PREFIX = "PENDING:PATIENT:";
    private static final String PENDING_CARRIER_PREFIX = "PENDING:CARRIER:";

    public Integer getOtp(String email) {
        return (Integer) redisTemplate.opsForValue().get(OTP_PREFIX + email);
    }

    public Integer getPatientOtp(String email) {
        return (Integer) redisTemplate.opsForValue().get(PATIENT_OTP_PREFIX + email);
    }

    public Integer getCarrierOtp(String email) {
        return (Integer) redisTemplate.opsForValue().get(CARRIER_OTP_PREFIX + email);
    }

    public PatientRequest getPendingPatient(String email) {
        try {
            String json = (String) redisTemplate.opsForValue().get(PENDING_PATIENT_PREFIX + email);
            if (json == null) return null;
            return objectMapper.readValue(json, PatientRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("error while parsing customer json from redis", e);
        }
    }

    public CarrierRequest getPendingCarrier(String email) {
        try {
            String json = (String) redisTemplate.opsForValue().get(PENDING_CARRIER_PREFIX + email);
            if (json == null) return null;
            return objectMapper.readValue(json, CarrierRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("error while parsing carrier json from redis", e);
        }
    }

    public void storeOtp(String email, Integer otp) {
        redisTemplate.opsForValue().set(OTP_PREFIX + email, otp, Duration.ofMinutes(5));
    }

    public void storePatientOtp(String email, Integer otp) {
        redisTemplate.opsForValue().set(PATIENT_OTP_PREFIX + email, otp, Duration.ofMinutes(5));
    }

    public void storeCarrierOtp(String email, Integer otp) {
        redisTemplate.opsForValue().set(CARRIER_OTP_PREFIX + email, otp, Duration.ofMinutes(5));
    }

    public void storePendingPatient(String email, PatientRequest patientRequest) {
        try {
            String json = objectMapper.writeValueAsString(patientRequest);
            redisTemplate.opsForValue().set(PENDING_PATIENT_PREFIX + email, json, Duration.ofMinutes(5));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("error while parsing customer json from redis", e);
        }
    }

    public void storePendingCarrier(String email, CarrierRequest carrierRequest) {
        try {
            String json = objectMapper.writeValueAsString(carrierRequest);
            redisTemplate.opsForValue().set(PENDING_CARRIER_PREFIX + email, json, Duration.ofMinutes(5));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("error while serializing carrier json to redis", e);
        }
    }

    public void deleteOtp(String email) {
        redisTemplate.delete(OTP_PREFIX + email);
    }

    public void deletePatientOtp(String email) {
        redisTemplate.delete(PATIENT_OTP_PREFIX + email);
    }

    public void deleteCarrierOtp(String email) {
        redisTemplate.delete(CARRIER_OTP_PREFIX + email);
    }

    public void deletePendingPatient(String email) {
        redisTemplate.delete(PENDING_PATIENT_PREFIX + email);
    }

    public void deletePendingCarrier(String email) {
        redisTemplate.delete(PENDING_CARRIER_PREFIX + email);
    }

    public Integer getOtpAttempts(String email) {
        Integer attempts = (Integer) redisTemplate.opsForValue().get("OTP_ATTEMPT:" + email);
        return attempts == null ? 0 : attempts;
    }

    public void incrementOtpAttempt(String email) {
        String key = "OTP_ATTEMPT:" + email;
        redisTemplate.opsForValue().increment(key);
        redisTemplate.expire(key, Duration.ofMinutes(5));
    }

    public void deleteOtpAttempt(String email) {
        redisTemplate.delete("OTP_ATTEMPT:" + email);
    }

    public boolean isPendingPatientExists(String email) {
        return getPendingPatient(email) != null;
    }

    public boolean isPendingCarrierExists(String email) {
        return getPendingCarrier(email) != null;
    }
}
