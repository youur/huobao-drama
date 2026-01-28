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
@Table(name = "video_generations")
@Where(clause = "deleted_at IS NULL")
public class VideoGeneration extends BaseEntity {
    @Column(name = "storyboard_id")
    private Long storyboardId;

    @Column(name = "drama_id", nullable = false)
    private Long dramaId;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String prompt;

    private String model;

    @Column(name = "image_gen_id")
    private Long imageGenId;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "first_frame_url")
    private String firstFrameUrl;

    private Integer duration;

    private Integer fps;

    private String resolution;

    @Column(name = "aspect_ratio")
    private String aspectRatio;

    private String style;

    @Column(name = "motion_level")
    private Integer motionLevel;

    @Column(name = "camera_motion")
    private String cameraMotion;

    private Long seed;

    @Column(name = "video_url")
    private String videoUrl;

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

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    private Integer width;

    private Integer height;
}
