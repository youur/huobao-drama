package com.huobao.drama.domain.repository;

import com.huobao.drama.domain.model.CharacterLibrary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CharacterLibraryRepository extends JpaRepository<CharacterLibrary, Long> {
    List<CharacterLibrary> findByCategory(String category);
}
