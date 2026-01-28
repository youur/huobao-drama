import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class GenerateVideoRequest {
    @JsonProperty("storyboard_id")
    private Long storyboardId;
    
    @JsonProperty("drama_id")
    private Long dramaId;
    
    @JsonProperty("image_gen_id")
    private Long imageGenId;
    
    @JsonProperty("reference_mode")
    private String referenceMode; // single, first_last, multiple, none
    
    @JsonProperty("image_url")
    private String imageUrl;
    
    @JsonProperty("first_frame_url")
    private String firstFrameUrl;
    
    @JsonProperty("last_frame_url")
    private String lastFrameUrl;
    
    @JsonProperty("reference_image_urls")
    private List<String> referenceImageUrls;
    
    private String prompt;
    private String provider;
    private String model;
    private Integer duration;
    private Integer fps;
    
    @JsonProperty("aspect_ratio")
    private String aspectRatio;
    
    private String style;
    
    @JsonProperty("motion_level")
    private Integer motionLevel;
    
    @JsonProperty("camera_motion")
    private String cameraMotion;
    
    private Long seed;
}
