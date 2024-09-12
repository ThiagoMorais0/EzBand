package com.tms.tmsis.foodhub.service.impl;

import com.tms.tmsis.core.dto.RetornoDTO;
import com.tms.tmsis.foodhub.dao.FHProdutoDao;
import com.tms.tmsis.foodhub.dto.FHProdutoDTO;
import com.tms.tmsis.foodhub.enums.FHTipoProduto;
import com.tms.tmsis.foodhub.model.FHProduto;
import com.tms.tmsis.foodhub.service.FHProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class FHProdutoServiceImpl implements FHProdutoService {

    @Autowired
    private FHProdutoDao fhProdutoDao;

    @Override
    public RetornoDTO cadastrarProduto(FHProdutoDTO produtoDTO) {
        try {
            Optional<FHProduto> optionalProduto = fhProdutoDao.findById(produtoDTO.getCodigo());

            if (optionalProduto.isPresent()) {
                FHProduto produto = optionalProduto.get();
                produto.setNome(produtoDTO.getNome());
                produto.setTipo(FHTipoProduto.findByCodigo(produtoDTO.getTipo()));
                produto.setValorCompra(produtoDTO.getValorCompra());
                produto.setValorVenda(produtoDTO.getValorVenda());
                produto.setDescricao(produtoDTO.getDescricao());
                produto.setAtivo(produtoDTO.getAtivo());
                produto.setDataAlteracao(new Date());
                fhProdutoDao.save(produto);
                return RetornoDTO.success("Produto alterado com sucesso!");
            } else {
                FHProduto novoProduto = new FHProduto(produtoDTO);
                fhProdutoDao.save(novoProduto);
                return RetornoDTO.success("Produto cadastrado com sucesso!");
            }
        } catch (Exception e) {
            return RetornoDTO.error(e.getMessage());
        }
    }

    @Override
    public RetornoDTO buscarTodosProdutos() {
        return RetornoDTO.success(fhProdutoDao.findAll());
    }

}
