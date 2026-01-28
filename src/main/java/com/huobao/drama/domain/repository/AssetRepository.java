package com.huobao.drama.domain.repository;

import com.huobao.drama.domain.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {
    List<Asset> findByDramaId(Long dramaId);
    List<Asset> findByType(String type);
}
