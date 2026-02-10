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
public class InstanceRequest {
    
    @JsonProperty("instanceName")
    private String instanceName;
    
    @JsonProperty("qrcode")
    private Boolean qrcode;
    
    @JsonProperty("integration")
    private String integration;
}
