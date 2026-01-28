package com.huobao.drama.domain.model;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "async_tasks")
@Where(clause = "deleted_at IS NULL")
public class AsyncTask {
    @Id
    @Column(length = 36)
    private String id;

    @Column(length = 50, nullable = false)
    private String type;

    @Column(length = 20, nullable = false)
    private String status;

    @Column(nullable = false)
    private Integer progress = 0;

    @Column(length = 500)
    private String message;

    private String error;

    private String result;

    @Column(name = "resource_id", length = 36)
    private String resourceId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
