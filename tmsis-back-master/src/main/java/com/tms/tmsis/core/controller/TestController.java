package com.tms.tmsis.core.controller;

import com.tms.tmsis.core.dto.RetornoDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("teste")
public class TestController {

    @GetMapping("/teste.do")
    public RetornoDTO teste(){
     return RetornoDTO.success("Sucesso");
    }
}
