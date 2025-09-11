package com.example.authservice.interfaces.rest;

import com.example.authservice.application.auth.LogoutHandler; 
import com.example.authservice.application.auth.PasswordLoginHandler;
import com.example.authservice.application.auth.RefreshTokenHandler; 
import com.example.authservice.interfaces.rest.dto.auth.PasswordLoginRequest;
import com.example.authservice.interfaces.rest.dto.auth.RefreshTokenRequest; 
import com.example.authservice.interfaces.rest.dto.auth.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final PasswordLoginHandler passwordLoginHandler;
    private final RefreshTokenHandler refreshTokenHandler; 
    private final LogoutHandler logoutHandler; 

    @PostMapping("/login/password")
    public ResponseEntity<TokenResponse> loginWithPassword(@Valid @RequestBody PasswordLoginRequest request) {
        TokenResponse token = passwordLoginHandler.handle(
            request.email(),
            request.password()
        );
        return ResponseEntity.ok(token);
    }

    // Endpoint para renovar o token de acesso
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse token = refreshTokenHandler.handle(request.refreshToken());
        return ResponseEntity.ok(token);
    }

    // Endpoint para fazer logout (invalidar o refresh token)
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
        logoutHandler.handle(request.refreshToken());
        return ResponseEntity.noContent().build(); // Retorna 204 No Content (sucesso sem corpo)
    }
}