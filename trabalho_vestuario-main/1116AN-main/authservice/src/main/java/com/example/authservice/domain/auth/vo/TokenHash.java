package com.example.authservice.domain.auth.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Embeddable // Indica que esta classe será embutida em outra entidade
@Getter
@NoArgsConstructor // Necessário para o JPA
public class TokenHash {

    @Column(name = "token_hash", nullable = false, unique = true)
    private String value;

    // O construtor é privado para forçar o uso do método "of"
    private TokenHash(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) {
            throw new IllegalArgumentException("Token não pode ser nulo ou vazio");
        }
        this.value = hash(rawToken);
    }

    // Método estático para criar uma instância (padrão de projeto "Factory Method")
    public static TokenHash of(String rawToken) {
        return new TokenHash(rawToken);
    }

    // Lógica para gerar o hash SHA-256
    private String hash(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            // Se o algoritmo SHA-256 não existir no Java (o que é quase impossível)
            throw new RuntimeException("Erro ao gerar hash do token", e);
        }
    }
}