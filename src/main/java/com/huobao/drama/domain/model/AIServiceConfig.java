package com.huobao.drama.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ai_service_configs")
@Where(clause = "deleted_at IS NULL")
public class AIServiceConfig extends BaseEntity {
    @Column(name = "service_type", nullable = false)
    private String serviceType; // text, image, video

    private String provider;

    @Column(nullable = false)
    private String name;

    @Column(name = "base_url", nullable = false)
    private String baseUrl;

    @Column(name = "api_key", nullable = false)
    private String apiKey;

    private String model;

    private String endpoint;

    @Column(name = "query_endpoint")
    private String queryEndpoint;

    @Column(nullable = false)
    private Integer priority = 0;

    @Column(name = "is_default", nullable = false)
    private Integer isDefault = 0;

    @Column(name = "is_active", nullable = false)
    private Integer isActive = 1;

    private String settings; // JSON string
}
