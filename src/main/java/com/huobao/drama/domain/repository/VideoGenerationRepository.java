package com.huobao.drama.domain.repository;

import com.huobao.drama.domain.model.VideoGeneration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VideoGenerationRepository extends JpaRepository<VideoGeneration, Long> {
    List<VideoGeneration> findByStoryboardId(Long storyboardId);
    List<VideoGeneration> findByTaskId(String taskId);
}
