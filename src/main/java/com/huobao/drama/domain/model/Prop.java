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
@Table(name = "props")
@Where(clause = "deleted_at IS NULL")
public class Prop extends BaseEntity {
    @Column(name = "drama_id", nullable = false)
    private Long dramaId;

    @Column(nullable = false)
    private String name;

    private String type;

    private String description;

    private String prompt;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "local_path")
    private String localPath;

    @Column(name = "reference_images")
    private String referenceImages; // JSON string
}
