package com.huobao.drama.domain.repository;

import com.huobao.drama.domain.model.Episode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EpisodeRepository extends JpaRepository<Episode, Long> {
    List<Episode> findByDramaId(Long dramaId);
}
