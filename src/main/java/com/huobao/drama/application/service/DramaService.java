package com.huobao.drama.application.service;

import com.huobao.drama.api.dto.DramaDTO;
import com.huobao.drama.domain.model.Drama;
import com.huobao.drama.domain.model.DramaCharacter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

public interface DramaService {
    Drama createDrama(DramaDTO dramaDTO);
    Drama getDrama(Long id);
    Page<Drama> listDramas(String status, String genre, String keyword, Pageable pageable);
    Drama updateDrama(Long id, DramaDTO dramaDTO);
    void deleteDrama(Long id);
    
    // --- 新增方法 ---
    Map<String, Object> getStats();
    Drama saveOutline(Long id, Map<String, Object> outlineData);
    List<DramaCharacter> getCharacters(Long dramaId);
    void saveCharacters(Long id, List<Map<String, Object>> charactersData);
    void saveProgress(Long id, String currentStep, String stepData);
}
