package com.example.authservice.application.auth;

import com.example.authservice.application.ports.PasswordHasher;
import com.example.authservice.application.ports.TokenService;
import com.example.authservice.domain.auth.RefreshToken; 
import com.example.authservice.domain.auth.RefreshTokenRepository; 
import com.example.authservice.domain.user.User;
import com.example.authservice.domain.user.UserRepository;
import com.example.authservice.domain.user.vo.Email;
import com.example.authservice.infrastructure.config.JwtProperties; 
import com.example.authservice.interfaces.rest.dto.auth.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant; 
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordLoginHandler {
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final RefreshTokenRepository refreshTokenRepository; 
    private final JwtProperties jwtProperties; 

    public TokenResponse handle(String emailRaw, String passwordRaw) {
        Email email = Email.of(emailRaw);
        Optional<User> userOpt = userRepository.findByEmail(email.getValue());

        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credencial invalida");
        }

        User user = userOpt.get();
        if (!passwordHasher.matches(passwordRaw, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credencial invalida");
        }

        TokenService.TokenPair pair = tokenService.issue(user);

        Instant expiryDate = Instant.now().plusSeconds(jwtProperties.getRefreshTtlSeconds());
        RefreshToken refreshToken = new RefreshToken(pair.refreshToken(), user, expiryDate);
        refreshTokenRepository.save(refreshToken);

        return new TokenResponse(
                pair.accessToken(),
                pair.refreshToken(),
                pair.expiresInSeconds()
        );
    }
}
