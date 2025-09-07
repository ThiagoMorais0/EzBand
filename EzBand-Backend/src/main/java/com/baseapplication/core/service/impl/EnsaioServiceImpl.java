package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.EnsaioDao;
import com.baseapplication.core.enums.SituacaoMusicoEvento;
import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.exception.ResourceNotFoundException;
import com.baseapplication.core.model.*;
import com.baseapplication.core.service.EnsaioService;
import com.baseapplication.core.service.MusicoEventoService;
import jakarta.persistence.EntityManager;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnsaioServiceImpl implements EnsaioService {

    @Autowired
    private EnsaioDao ensaioDao;

    @Autowired
    private MusicoEventoService musicoEventoService;

    @Autowired
    private EntityManager entityManager;

    @Override
    public Ensaio buscarPorId(Long idEvento) {
        return ensaioDao.findById(idEvento).orElseThrow(() -> new ResourceNotFoundException("Ensaio não encontrado"));
    }

    @Override
    public List<Ensaio> buscarPendentesPorUsuarioOrdenadoPorData(Long idUsuario) {
        return ensaioDao.buscarPorIdUsuario(idUsuario)
                .stream()
                .filter(i -> i.getStatus().equals(StatusEvento.PENDENTE))
                .sorted(Comparator.comparing(Ensaio::getData))
                .collect(Collectors.toList());
    }

    @Override
    public Ensaio salvar(Ensaio ensaio) {
        return ensaioDao.save(ensaio);
    }

    @Override
    public List<Ensaio> buscarEnsaiosPorStatusBandaEUsuarioOrdenadoPorData(Long idBanda, Long idUsuario, String status) {
        return ensaioDao.buscarEnsaiosPorStatusBandaEUsuarioOrdenadoPorData(idBanda, idUsuario, status);
    }

    @Override
    public void alterarStatus(Long idEnsaio, StatusEvento novoStatus) {
        Ensaio ensaio = buscarPorId(idEnsaio);
        ensaio.setStatus(novoStatus);
        salvar(ensaio);
    }

    @Override
    public List<Ensaio> buscarComDataAnteriorAHoje() {
        return ensaioDao.buscarComDataAnteriorAHoje(LocalDate.now(), StatusEvento.REALIZADO);
    }

    @Override
    public void adicionarMusicoAoEnsaio(Long idEvento, Long idUsuarioConvidado) {
        MusicoEvento musicoEvento = new MusicoEvento();
        musicoEvento.setUsuario(entityManager.find(Usuario.class, idUsuarioConvidado));
        musicoEvento.setEvento(entityManager.find(Ensaio.class, idEvento));
        musicoEvento.setSituacao(SituacaoMusicoEvento.ATIVO);
        musicoEventoService.salvar(musicoEvento);
    }
}
