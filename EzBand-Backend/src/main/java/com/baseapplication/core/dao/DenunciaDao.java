package com.baseapplication.core.dao;

import com.baseapplication.core.model.Denuncia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DenunciaDao extends JpaRepository<Denuncia, Long> {

    boolean existsByDenuncianteIdAndUsuarioDenunciadoId(Long idDenunciante, Long idUsuarioDenunciado);

    boolean existsByDenuncianteIdAndBandaDenunciadaId(Long idDenunciante, Long idBandaDenunciada);
}
