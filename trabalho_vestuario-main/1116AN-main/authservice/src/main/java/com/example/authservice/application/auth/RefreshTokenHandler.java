package com.example.authservice.application.auth;

import com.example.authservice.application.ports.TokenService;
import com.example.authservice.domain.auth.RefreshToken;
import com.example.authservice.domain.auth.RefreshTokenRepository;
import com.example.authservice.domain.auth.vo.TokenHash;
import com.example.authservice.infrastructure.config.JwtProperties;
import com.example.authservice.interfaces.rest.dto.auth.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RefreshTokenHandler {

    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;
    private final JwtProperties jwtProperties;
    
    @Transactional // Garante que todas as operações com o banco aqui ou acontecem ou são desfeitas
    public TokenResponse handle(String rawRefreshToken) {
        // 1. Calcula o hash do token recebido para procurar no banco
        String refreshTokenHash = TokenHash.of(rawRefreshToken).getValue();

        // 2. Procura o token no banco. Se não achar, lança um erro.
        RefreshToken refreshToken = refreshTokenRepository.findActiveByHash(refreshTokenHash)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido ou revogado"));
        
        // 3. Verifica se o token encontrado já expirou (uma dupla checagem)
        if (!refreshToken.isActive()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expirado. Faça login novamente.");
        }
        
        // 4. Revoga (invalida) o token antigo. Isso impede que ele seja usado novamente.
        refreshTokenRepository.revoke(refreshToken);
        
        // 5. Emite um novo par de tokens (um novo access e um novo refresh)
        TokenService.TokenPair newPair = tokenService.issue(refreshToken.getUser());
        
        // 6. Cria e salva o novo refresh token no banco
        Instant newExpiryDate = Instant.now().plusSeconds(jwtProperties.getRefreshTtlSeconds());
        RefreshToken newRefreshToken = new RefreshToken(newPair.refreshToken(), refreshToken.getUser(), newExpiryDate);
        refreshTokenRepository.save(newRefreshToken);

        // 7. Retorna o novo par de tokens para o usuário
        return new TokenResponse(
                newPair.accessToken(),
                newPair.refreshToken(), // Retorna o NOVO refresh token
                newPair.expiresInSeconds()
        );
    }
}