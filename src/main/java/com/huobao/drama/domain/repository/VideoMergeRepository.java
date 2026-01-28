package com.huobao.drama.domain.repository;

import com.huobao.drama.domain.model.VideoMerge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VideoMergeRepository extends JpaRepository<VideoMerge, Long> {
    List<VideoMerge> findByEpisodeId(Long episodeId);
}
