package com.example.authservice.infrastructure.persistence;

import com.example.authservice.domain.auth.RefreshToken;
import com.example.authservice.domain.auth.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component // Marca como um componente do Spring (para poder ser injetado em outras classes)
@RequiredArgsConstructor // Cria um construtor com os campos 'final'
public class JpaRefreshTokenRepository implements RefreshTokenRepository {
    
    private final SpringDataRefreshTokenJpa jpa; // Injetamos a interface mágica do Spring

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return jpa.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findActiveByHash(String tokenHash) {
        return jpa.findByTokenHash_ValueAndRevokedIsFalse(tokenHash);
    }

    @Override
    public void revoke(RefreshToken token) {
        token.revoke(); // Marca o token como revogado
        jpa.save(token); // E salva a alteração no banco
    }
}