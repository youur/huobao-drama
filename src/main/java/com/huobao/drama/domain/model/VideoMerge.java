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
@Table(name = "video_merges")
@Where(clause = "deleted_at IS NULL")
public class VideoMerge extends BaseEntity {
    @Column(name = "episode_id", nullable = false)
    private Long episodeId;

    @Column(name = "drama_id", nullable = false)
    private Long dramaId;

    private String title;

    @Column(nullable = false)
    private String provider;

    private String model;

    @Column(nullable = false)
    private String status = "pending";

    @Column(nullable = false)
    private String scenes; // JSON string

    @Column(name = "merged_url")
    private String mergedUrl;

    private Integer duration;

    @Column(name = "task_id")
    private String taskId;

    @Column(name = "error_msg")
    private String errorMsg;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
