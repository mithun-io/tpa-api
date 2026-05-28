package com.tpa.service.impl;

import com.tpa.entity.RefreshToken;
import com.tpa.entity.User;
import com.tpa.exception.NoResourceFoundException;
import com.tpa.repository.RefreshTokenRepository;
import com.tpa.repository.UserRepository;
import com.tpa.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

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

    @Override
    @Transactional
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NoResourceFoundException("User not found"));

        refreshTokenRepository.deleteByUser_Id(userId);

        String rawToken = UUID.randomUUID().toString();
        String hashedToken = hashToken(rawToken);

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(hashedToken)
                .rawToken(rawToken)
                .expiryDate(LocalDateTime.now().plusDays(7)) // 7 days expiry
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken refreshToken) {
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }

        return refreshToken;
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUser_Id(userId);
    }
}
