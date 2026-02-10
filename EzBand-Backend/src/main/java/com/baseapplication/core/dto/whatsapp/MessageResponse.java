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
public class MessageResponse {
    
    @JsonProperty("key")
    private MessageKey key;
    
    @JsonProperty("message")
    private Message message;
    
    @JsonProperty("messageTimestamp")
    private Long messageTimestamp;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessageKey {
        @JsonProperty("remoteJid")
        private String remoteJid;
        
        @JsonProperty("fromMe")
        private Boolean fromMe;
        
        @JsonProperty("id")
        private String id;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        @JsonProperty("extendedTextMessage")
        private ExtendedTextMessage extendedTextMessage;
        
        @JsonProperty("conversation")
        private String conversation;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExtendedTextMessage {
        @JsonProperty("text")
        private String text;
    }
}
