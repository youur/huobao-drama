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
@Table(name = "storyboards")
@Where(clause = "deleted_at IS NULL")
public class Storyboard extends BaseEntity {
    @Column(name = "episode_id", nullable = false)
    private Long episodeId;

    @Column(name = "scene_id")
    private Long sceneId;

    @Column(name = "storyboard_number", nullable = false)
    private Integer storyboardNumber;

    private String title;

    private String description;

    private String location;

    private String time;

    @Column(nullable = false)
    private Integer duration = 0;

    private String dialogue;

    private String action;

    private String atmosphere;

    @Column(name = "image_prompt")
    private String imagePrompt;

    @Column(name = "video_prompt")
    private String videoPrompt;

    private String characters; // JSON string

    @Column(name = "composed_image")
    private String composedImage;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(nullable = false)
    private String status = "pending";
}
