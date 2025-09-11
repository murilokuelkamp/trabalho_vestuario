package com.example.authservice.domain.auth;

import com.example.authservice.domain.auth.vo.TokenHash;
import com.example.authservice.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Entity // Marca como uma tabela do banco de dados
@Table(name = "refresh_tokens") // Define o nome da tabela
@Getter
@NoArgsConstructor // Necessário para o JPA
public class RefreshToken {

    @Id // Marca este campo como a chave primária (Primary Key)
    @GeneratedValue(strategy = GenerationType.UUID) // Gera o valor do ID automaticamente
    private UUID id;

    @Embedded // Embutimos o Objeto de Valor aqui
    private TokenHash tokenHash;

    @Column(nullable = false)
    private Instant expiresAt; // O "expiresAt" solicitado

    @Column(nullable = false)
    private boolean revoked = false; // "revoked" significa "revogado" ou "invalidado"

    @ManyToOne(fetch = FetchType.LAZY) // Define o relacionamento: Muitos tokens para Um usuário
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    public RefreshToken(String rawToken, User user, Instant expiresAt) {
        this.tokenHash = TokenHash.of(rawToken); // Usa nosso objeto de valor
        this.user = user;
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiresAt);
    }

    public boolean isActive() {
        // Um token está ativo se NÃO foi revogado E NÃO expirou
        return !this.revoked && !isExpired();
    }
    
    public void revoke() {
        this.revoked = true;
    }
}