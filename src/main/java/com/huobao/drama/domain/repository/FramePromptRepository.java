package com.huobao.drama.domain.repository;

import com.huobao.drama.domain.model.FramePrompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FramePromptRepository extends JpaRepository<FramePrompt, Long> {
    List<FramePrompt> findByStoryboardId(Long storyboardId);
}
