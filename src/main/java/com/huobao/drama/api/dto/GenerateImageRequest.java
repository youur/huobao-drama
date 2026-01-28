import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class GenerateImageRequest {
    @JsonProperty("storyboard_id")
    private Long storyboardId;
    
    @JsonProperty("drama_id")
    private Long dramaId;
    
    @JsonProperty("scene_id")
    private Long sceneId;
    
    @JsonProperty("character_id")
    private Long characterId;
    
    @JsonProperty("prop_id")
    private Long propId;
    
    @JsonProperty("image_type")
    private String imageType; // character, scene, storyboard
    
    @JsonProperty("frame_type")
    private String frameType; // first, key, last, panel, action
    
    private String prompt;
    
    @JsonProperty("negative_prompt")
    private String negativePrompt;
    
    private String provider;
    private String model;
    private String size;
    private String quality;
    private String style;
    private Integer steps;
    
    @JsonProperty("cfg_scale")
    private Double cfgScale;
    
    private Long seed;
    private Integer width;
    private Integer height;
    
    @JsonProperty("reference_images")
    private List<String> referenceImages;
}
