package com.tms.tmsis.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public final class RetornoDTO {

    private Integer success;
    private Object object;
    private String message;
    private List<Object> objects;

    public static RetornoDTO success(){
        return new RetornoDTO(1, null, null, null);
    }

    public static RetornoDTO success(String message){
        return new RetornoDTO(1, null, message, null);
    }

    public static RetornoDTO success(Object object){
        return new RetornoDTO(1, object, null, null);
    }

    public static RetornoDTO success(String message, Object object){
        return new RetornoDTO(1, object, message, null);
    }

    public static RetornoDTO success(List<Object> object){
        return new RetornoDTO(1, null, null, object);
    }

    public static RetornoDTO error(){
        return new RetornoDTO(0, null, null, null);
    }

    public static RetornoDTO error(String message){
        return new RetornoDTO(0, null, message, null);
    }



}
