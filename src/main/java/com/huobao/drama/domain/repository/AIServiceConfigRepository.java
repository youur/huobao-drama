package com.huobao.drama.domain.repository;

import com.huobao.drama.domain.model.AIServiceConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AIServiceConfigRepository extends JpaRepository<AIServiceConfig, Long> {
    List<AIServiceConfig> findByServiceTypeAndIsActive(String serviceType, Integer isActive);
}
