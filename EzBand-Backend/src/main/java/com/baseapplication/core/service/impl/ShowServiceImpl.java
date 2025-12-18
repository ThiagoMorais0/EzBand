package com.baseapplication.core.service.impl;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.baseapplication.core.enums.SituacaoMusicoEvento;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.*;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baseapplication.core.dao.ShowDao;
import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.exception.ResourceNotFoundException;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.service.ShowService;

@Service
public class ShowServiceImpl implements ShowService {

	@Autowired
	private ShowDao showDao;

	@Autowired
	private MusicoEventoServiceImpl musicoEventoService;

	@Autowired
	private EntityManager entityManager;

	@Override
	public Show buscarPorId(Long idShow) {
		return showDao.findById(idShow).orElseThrow(() -> new ResourceNotFoundException("Show (" + idShow + ") não encontrado"));
	}

	@Override
	public List<Show> buscarPendentesPorUsuarioOrdenadoPorData(Long idUsuario) {
		return showDao.buscarPorIdUsuario(idUsuario).stream()
				.filter(i -> i.getStatus().equals(StatusEvento.PENDENTE))
				.filter(i -> i.getData() != null)
				.sorted(Comparator.comparing(Show::getData)).collect(Collectors.toList());
	}

	@Override
	public Show salvar(Show show) {
		return showDao.save(show);
	}

	@Override
	public List<RepertorioEvento> buscarRepertorio(Long idEvento) {
		return buscarPorId(idEvento).getRepertorio();
	}

	@Override
	public List<Show> buscarShowsPendentesPorBandaEUsuarioOrdenadoPorData(Long idBanda, Long idUsuario) {
		return showDao.buscarShowsPorStatusBandaEUsuarioOrdenadoPorData(idBanda, idUsuario,
				StatusEvento.PENDENTE.toString());
	}

	@Override
	public List<Show> buscarShowsAguardandoPorBandaEUsuarioOrdenadoPorData(Long idBanda, Long idUsuario) {
		return showDao.buscarShowsPorStatusBandaEUsuarioOrdenadoPorData(idBanda, idUsuario,
				StatusEvento.AGUARDANDO_APROVACAO.toString());
	}

	@Override
	public List<Show> buscarShowsPorStatusBandaEUsuarioOrdenadoPorData(Long idBanda, Long idUsuario, String status) {
		return showDao.buscarShowsPorStatusBandaEUsuarioOrdenadoPorData(idBanda, idUsuario, status);
	}

	@Override
	public Evento buscarPrimeiroPorUsuarioEData(Long idUsuario, LocalDate data) {
		return showDao.buscarPrimeiroPorUsuarioEData(idUsuario, data);
	}

	@Override
	public List<Evento> buscarPorUsuarioEData(Long idUsuario, LocalDate data) {
		return showDao.buscarPorUsuarioEData(idUsuario, data);
	}

    @Override
    public void alterarStatus(Long idShow, StatusEvento novoStatus) {
		Show show = buscarPorId(idShow);
		show.setStatus(novoStatus);
		salvar(show);
    }

    @Override
    public List<Show> buscarComDataAnteriorAHoje() {
        return showDao.buscarComDataAnteriorAHoje(LocalDate.now(), StatusEvento.REALIZADO);
    }

    @Override
    public void adicionarMusicoAoShow(Long idEvento, Long idUsuarioConvidado) {
		MusicoEvento musicoEvento = new MusicoEvento();
		musicoEvento.setUsuario(entityManager.find(Usuario.class, idUsuarioConvidado));
		musicoEvento.setEvento(entityManager.find(Show.class, idEvento));
		MusicoEventoId id = new MusicoEventoId();
		id.setIdEvento(idEvento);
		id.setIdUsuario(idUsuarioConvidado);
		id.setTipoEvento(TipoEvento.SHOW);
		musicoEvento.setId(id);
		musicoEvento.setSituacao(SituacaoMusicoEvento.ATIVO);
		musicoEventoService.salvar(musicoEvento);
    }

}
