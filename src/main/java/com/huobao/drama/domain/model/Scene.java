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
@Table(name = "scenes")
@Where(clause = "deleted_at IS NULL")
public class Scene extends BaseEntity {
    @Column(name = "drama_id", nullable = false)
    private Long dramaId;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String time;

    @Column(nullable = false)
    private String prompt;

    @Column(name = "storyboard_count", nullable = false)
    private Integer storyboardCount = 1;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "local_path")
    private String localPath;

    @Column(nullable = false)
    private String status = "pending";
}
