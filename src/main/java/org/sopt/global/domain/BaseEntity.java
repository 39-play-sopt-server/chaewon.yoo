package org.sopt.global.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected BaseEntity() {}

    @PrePersist
    protected void initializeCreatedAt() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return this.id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
