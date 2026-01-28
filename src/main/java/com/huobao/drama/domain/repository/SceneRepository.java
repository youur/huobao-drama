package com.huobao.drama.domain.repository;

import com.huobao.drama.domain.model.Scene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SceneRepository extends JpaRepository<Scene, Long> {
    List<Scene> findByDramaId(Long dramaId);
}
