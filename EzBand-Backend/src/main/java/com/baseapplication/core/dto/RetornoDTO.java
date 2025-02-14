package com.baseapplication.core.dto;

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

    public static RetornoDTO success(){
        return new RetornoDTO(1, null, null);
    }

    public static RetornoDTO success(String message){
        return new RetornoDTO(1, null, message);
    }

    public static RetornoDTO success(Object object){
        return new RetornoDTO(1, object, null);
    }

    public static RetornoDTO success(String message, Object object){
        return new RetornoDTO(1, object, message);
    }


    public static RetornoDTO error(){
        return new RetornoDTO(0, null, null);
    }

//    public static RetornoDTO error(String message){
//        return new RetornoDTO(0, null, "Erro: " + message);
//    }
    public static RetornoDTO error(String message){
        return new RetornoDTO(0, null, message);
    }



}
