package com.huobao.drama.domain.repository;

import com.huobao.drama.domain.model.DramaCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DramaCharacterRepository extends JpaRepository<DramaCharacter, Long> {
    List<DramaCharacter> findByDramaId(Long dramaId);
}
