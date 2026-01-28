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
@Table(name = "character_libraries")
@Where(clause = "deleted_at IS NULL")
public class CharacterLibrary extends BaseEntity {
    @Column(nullable = false)
    private String name;

    private String category;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "local_path")
    private String localPath;

    private String description;

    private String tags;

    @Column(name = "source_type", nullable = false)
    private String sourceType = "generated";
}
