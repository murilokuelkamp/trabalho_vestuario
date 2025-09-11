package com.example.authservice.application.auth;

import com.example.authservice.domain.auth.RefreshTokenRepository;
import com.example.authservice.domain.auth.vo.TokenHash;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogoutHandler {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void handle(String rawRefreshToken) {
        String refreshTokenHash = TokenHash.of(rawRefreshToken).getValue();
        // Procura o token e, se encontrar, chama o método para revogá-lo
        refreshTokenRepository.findActiveByHash(refreshTokenHash)
            .ifPresent(refreshTokenRepository::revoke);
    }
}