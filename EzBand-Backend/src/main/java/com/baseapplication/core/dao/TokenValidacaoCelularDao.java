package com.baseapplication.core.dao;

import com.baseapplication.core.model.TokenValidacaoCelular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TokenValidacaoCelularDao extends JpaRepository<TokenValidacaoCelular, Long> {
    
    @Query("SELECT t FROM TokenValidacaoCelular t WHERE t.celular = :celular AND t.token = :token " +
           "AND t.validado = false AND t.dataExpiracao > :agora ORDER BY t.dataCriacao DESC")
    Optional<TokenValidacaoCelular> buscarTokenValido(String celular, String token, LocalDateTime agora);
    
    @Query("SELECT t FROM TokenValidacaoCelular t WHERE t.celular = :celular " +
           "AND t.validado = false AND t.dataExpiracao > :agora ORDER BY t.dataCriacao DESC")
    Optional<TokenValidacaoCelular> buscarUltimoTokenValidoPorCelular(String celular, LocalDateTime agora);
    
    @Query("SELECT t FROM TokenValidacaoCelular t WHERE t.idMembroFantasma = :idMembroFantasma " +
           "AND t.validado = false AND t.dataExpiracao > :agora ORDER BY t.dataCriacao DESC")
    Optional<TokenValidacaoCelular> buscarTokenValidoPorMembroFantasma(Long idMembroFantasma, LocalDateTime agora);
    
    @Query("SELECT t FROM TokenValidacaoCelular t WHERE t.idUsuario = :idUsuario " +
           "AND t.validado = false AND t.dataExpiracao > :agora ORDER BY t.dataCriacao DESC")
    Optional<TokenValidacaoCelular> buscarTokenValidoPorUsuario(Long idUsuario, LocalDateTime agora);
}
