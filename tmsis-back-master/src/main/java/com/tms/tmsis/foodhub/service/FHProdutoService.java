package com.tms.tmsis.foodhub.service;

import com.tms.tmsis.core.dto.RetornoDTO;
import com.tms.tmsis.foodhub.dto.FHProdutoDTO;

public interface FHProdutoService {
    RetornoDTO cadastrarProduto(FHProdutoDTO produtoDTO);

    RetornoDTO buscarTodosProdutos();
}
