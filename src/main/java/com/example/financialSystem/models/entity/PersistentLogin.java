package com.example.financialSystem.models.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entidade que representa a tabela persistent_logins.
 *
 * Obrigatória para o funcionamento do PersistentTokenBasedRememberMeServices
 * do Spring Security. O Hibernate cria/atualiza a tabela automaticamente
 * via spring.jpa.hibernate.ddl-auto=update.
 *
 * Campos:
 *  - series:    chave primária que identifica a sessão (nunca muda)
 *  - username:  identifica o usuário dono do token
 *  - token:     valor do token (rotacionado a cada requisição)
 *  - lastUsed:  data/hora do último uso
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "persistent_logins")
public class PersistentLogin {

    @Id
    @Column(name = "series", length = 64, nullable = false, unique = true)
    private String series;

    @Column(name = "username", length = 64, nullable = false)
    private String username;

    @Column(name = "token", length = 64, nullable = false)
    private String token;

    @Column(name = "last_used", nullable = false)
    private LocalDateTime lastUsed;
}

