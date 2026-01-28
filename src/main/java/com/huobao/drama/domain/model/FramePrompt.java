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
@Table(name = "frame_prompts")
@Where(clause = "deleted_at IS NULL")
public class FramePrompt extends BaseEntity {
    @Column(name = "storyboard_id", nullable = false)
    private Long storyboardId;

    @Column(name = "frame_type", nullable = false)
    private String frameType; // first, key, last, panel, action

    @Column(nullable = false)
    private String prompt;

    @Column(name = "negative_prompt")
    private String negativePrompt;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "local_path")
    private String localPath;

    @Column(nullable = false)
    private String status = "pending";
}
