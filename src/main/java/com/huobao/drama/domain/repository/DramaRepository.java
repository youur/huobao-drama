package com.huobao.drama.domain.repository;

import com.huobao.drama.domain.model.Drama;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DramaRepository extends JpaRepository<Drama, Long> {
}
