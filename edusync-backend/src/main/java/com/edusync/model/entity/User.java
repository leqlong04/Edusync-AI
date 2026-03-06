package com.edusync.model.entity;

import com.edusync.common.enums.SystemRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "users")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String password;

    @Builder.Default
    @Column(name = "reputation_score")
    private Integer reputationScore = 0;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    private SystemRole role = SystemRole.ROLE_USER;
}
