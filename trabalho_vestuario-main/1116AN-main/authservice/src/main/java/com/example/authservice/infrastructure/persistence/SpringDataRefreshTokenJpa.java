package com.example.authservice.infrastructure.persistence;

import com.example.authservice.domain.auth.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpringDataRefreshTokenJpa extends JpaRepository<RefreshToken, UUID> {
    
    // O Spring entende este nome e cria a query SQL:
    // "SELECT ... FROM refresh_tokens WHERE token_hash_value = ? AND revoked = false"
    Optional<RefreshToken> findByTokenHash_ValueAndRevokedIsFalse(String tokenHashValue);
}