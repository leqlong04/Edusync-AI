package com.edusync.model.entity;

import com.edusync.common.enums.JoinRequestStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "group_join_requests", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"group_id", "user_id", "status"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupJoinRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private JoinRequestStatus status = JoinRequestStatus.PENDING;
}
