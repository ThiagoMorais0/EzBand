package com.baseapplication.core.dto.whatsapp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMediaMessageRequest {
    
    @JsonProperty("number")
    private String number;
    
    @JsonProperty("mediatype")
    private String mediaType;
    
    @JsonProperty("mimetype")
    private String mimeType;
    
    @JsonProperty("caption")
    private String caption;
    
    @JsonProperty("media")
    private String media;
    
    @JsonProperty("fileName")
    private String fileName;
    
    @JsonProperty("delay")
    private Integer delay;
}
