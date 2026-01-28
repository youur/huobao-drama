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
@Table(name = "assets")
@Where(clause = "deleted_at IS NULL")
public class Asset extends BaseEntity {
    @Column(name = "drama_id")
    private Long dramaId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String type; // image, video, audio

    private String category;

    @Column(nullable = false)
    private String url;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "local_path")
    private String localPath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "mime_type")
    private String mimeType;

    private Integer width;

    private Integer height;

    private Integer duration;

    private String format;

    @Column(name = "image_gen_id")
    private Long imageGenId;

    @Column(name = "video_gen_id")
    private Long videoGenId;

    @Column(name = "is_favorite", nullable = false)
    private Integer isFavorite = 0;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;
}
