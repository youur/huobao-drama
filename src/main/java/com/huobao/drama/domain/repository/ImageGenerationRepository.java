package com.huobao.drama.domain.repository;

import com.huobao.drama.domain.model.ImageGeneration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ImageGenerationRepository extends JpaRepository<ImageGeneration, Long> {
    List<ImageGeneration> findByStoryboardId(Long storyboardId);
    List<ImageGeneration> findByTaskId(String taskId);
}
