package com.example.authservice.domain.auth;

import java.util.Optional;

public interface RefreshTokenRepository {
    
    // Salva um novo token ou atualiza um existente
    RefreshToken save(RefreshToken refreshToken);

    // Encontra um token ativo pelo seu hash
    Optional<RefreshToken> findActiveByHash(String tokenHash);

    // Revoga (invalida) um token
    void revoke(RefreshToken token);
}