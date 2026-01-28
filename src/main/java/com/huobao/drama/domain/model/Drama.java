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
@Table(name = "dramas")
@Where(clause = "deleted_at IS NULL")
public class Drama extends BaseEntity {
    @Column(nullable = false)
    private String title;

    private String description;

    private String genre;

    @Column(nullable = false)
    private String style = "realistic";

    @Column(name = "total_episodes", nullable = false)
    private Integer totalEpisodes = 1;

    @Column(name = "total_duration", nullable = false)
    private Integer totalDuration = 0;

    @Column(nullable = false)
    private String status = "draft";

    private String thumbnail;

    private String tags; // JSON string

    private String metadata; // JSON string
}
