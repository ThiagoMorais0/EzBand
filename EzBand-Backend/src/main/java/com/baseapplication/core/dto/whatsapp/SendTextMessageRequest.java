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
public class SendTextMessageRequest {
    
    @JsonProperty("number")
    private String number;
    
    @JsonProperty("text")
    private String text;
    
    @JsonProperty("delay")
    private Integer delay;
}
