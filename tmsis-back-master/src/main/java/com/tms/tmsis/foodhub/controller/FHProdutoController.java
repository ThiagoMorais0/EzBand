package com.tms.tmsis.foodhub.controller;

import com.tms.tmsis.core.dto.RetornoDTO;
import com.tms.tmsis.foodhub.dto.FHProdutoDTO;
import com.tms.tmsis.foodhub.service.FHProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/FHProduto")
public class FHProdutoController {

    @Autowired
    private FHProdutoService service;

    @PostMapping("/cadastrarProduto.do")
    public RetornoDTO cadastrarProduto(@RequestBody FHProdutoDTO produtoDTO){
        return service.cadastrarProduto(produtoDTO);
    }

    @GetMapping("/buscarTodosProdutos.do")
    public RetornoDTO buscarTodosProdutos(){
        return service.buscarTodosProdutos();
    }

}
