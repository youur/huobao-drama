package com.huobao.drama.api.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class DramaDTO {
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(max = 100)
    private String title;
    
    private String description;
    private String genre;
    private String style;
    private Integer totalEpisodes;
    private String status;
    private String thumbnail;
    private List<String> tags;
}
