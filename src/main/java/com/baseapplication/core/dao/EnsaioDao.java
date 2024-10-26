package com.baseapplication.core.dao;

import com.baseapplication.core.model.Ensaio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnsaioDao extends JpaRepository<Ensaio, Long> {
}
