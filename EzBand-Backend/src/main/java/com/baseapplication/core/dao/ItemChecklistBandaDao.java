package com.baseapplication.core.dao;

import com.baseapplication.core.model.ItemChecklistBanda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemChecklistBandaDao extends JpaRepository<ItemChecklistBanda, Long> {
    
    @Query("SELECT i FROM ItemChecklistBanda i WHERE i.idBanda = :idBanda ORDER BY i.dataCriacao DESC")
    List<ItemChecklistBanda> buscarPorIdBanda(Long idBanda);
    
    @Query("SELECT i FROM ItemChecklistBanda i WHERE i.idBanda = :idBanda ORDER BY i.dataCriacao DESC")
    List<ItemChecklistBanda> buscarTodosPorIdBanda(Long idBanda);
}
