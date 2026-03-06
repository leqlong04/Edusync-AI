package com.edusync.model.entity;

import com.edusync.common.enums.GroupVisibility;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Group extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private GroupVisibility visibility = GroupVisibility.PUBLIC;

    @Column(name = "join_code", unique = true, length = 10)
    private String joinCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    public boolean isPublic() {
        return visibility == GroupVisibility.PUBLIC;
    }

    public boolean isPrivate() {
        return visibility == GroupVisibility.PRIVATE;
    }

    public boolean isSecret() {
        return visibility == GroupVisibility.SECRET;
    }
}
