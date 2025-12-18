package com.baseapplication.core.dao;

import com.baseapplication.core.model.ItemChecklistUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemChecklistUsuarioDao extends JpaRepository<ItemChecklistUsuario, Long> {
    
    @Query("SELECT i FROM ItemChecklistUsuario i WHERE i.idUsuario = :idUsuario ORDER BY i.dataCriacao DESC")
    List<ItemChecklistUsuario> buscarPorIdUsuario(Long idUsuario);
    
    @Query("SELECT i FROM ItemChecklistUsuario i WHERE i.idUsuario = :idUsuario ORDER BY i.dataCriacao DESC")
    List<ItemChecklistUsuario> buscarTodosPorIdUsuario(Long idUsuario);
}
