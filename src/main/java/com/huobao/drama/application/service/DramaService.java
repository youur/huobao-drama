package com.huobao.drama.application.service;

import com.huobao.drama.api.dto.DramaDTO;
import com.huobao.drama.domain.model.Drama;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DramaService {
    Drama createDrama(DramaDTO dramaDTO);
    
    Drama getDrama(Long id);
    
    Page<Drama> listDramas(String status, String genre, String keyword, Pageable pageable);
    
    Drama updateDrama(Long id, DramaDTO dramaDTO);
    
    void deleteDrama(Long id);
}
