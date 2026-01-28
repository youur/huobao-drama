package com.huobao.drama.domain.repository;

import com.huobao.drama.domain.model.Prop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PropRepository extends JpaRepository<Prop, Long> {
    List<Prop> findByDramaId(Long dramaId);
}
