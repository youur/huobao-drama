package com.huobao.drama.domain.repository;

import com.huobao.drama.domain.model.Storyboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StoryboardRepository extends JpaRepository<Storyboard, Long> {
    List<Storyboard> findByEpisodeIdOrderByStoryboardNumberAsc(Long episodeId);
}
