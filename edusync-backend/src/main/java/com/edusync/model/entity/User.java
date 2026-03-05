package com.edusync.model.entity;

import com.edusync.common.enums.SystemRole;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    @Column(name = "reputation_score")
    private Integer reputationScore = 0;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SystemRole role = SystemRole.ROLE_USER;
}
