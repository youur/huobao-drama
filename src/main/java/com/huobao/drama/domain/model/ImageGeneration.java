package com.huobao.drama.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "image_generations")
@Where(clause = "deleted_at IS NULL")
public class ImageGeneration extends BaseEntity {
    @Column(name = "storyboard_id")
    private Long storyboardId;

    @Column(name = "drama_id", nullable = false)
    private Long dramaId;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String prompt;

    @Column(name = "negative_prompt")
    private String negativePrompt;

    private String model;

    private String size;

    private String quality;

    private String style;

    private Integer steps;

    @Column(name = "cfg_scale")
    private Double cfgScale;

    private Long seed;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "minio_url")
    private String minioUrl;

    @Column(name = "local_path")
    private String localPath;

    @Column(nullable = false)
    private String status = "pending";

    @Column(name = "task_id")
    private String taskId;

    @Column(name = "error_msg")
    private String errorMsg;

    private Integer width;

    private Integer height;

    @Column(name = "reference_images")
    private String referenceImages; // JSON string

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
