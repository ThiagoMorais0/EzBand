package com.baseapplication.core.dao;

import com.baseapplication.core.model.CustoOperacional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustoOperacionalDao extends JpaRepository<CustoOperacional, Long> {

    @Query("SELECT c FROM CustoOperacional c WHERE c.show.id = :idShow ORDER BY c.id ASC")
    List<CustoOperacional> buscarPorShow(Long idShow);
}
