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
@Table(name = "episodes")
@Where(clause = "deleted_at IS NULL")
public class Episode extends BaseEntity {
    @Column(name = "drama_id", nullable = false)
    private Long dramaId;

    @Column(name = "episode_number", nullable = false)
    private Integer episodeNumber;

    @Column(nullable = false)
    private String title;

    @Column(name = "script_content")
    private String scriptContent;

    private String description;

    @Column(nullable = false)
    private Integer duration = 0;

    @Column(nullable = false)
    private String status = "draft";

    @Column(name = "video_url")
    private String videoUrl;

    private String thumbnail;
}
