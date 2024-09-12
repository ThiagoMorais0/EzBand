package com.tms.tmsis.foodhub.dao;

import com.tms.tmsis.foodhub.model.FHProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FHProdutoDao extends JpaRepository<FHProduto, String> {
}
