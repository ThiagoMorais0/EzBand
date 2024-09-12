package com.baseapplication.core.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;

public interface Evento<T, ID> {
    JpaRepository<T, ID> getDao();
}
