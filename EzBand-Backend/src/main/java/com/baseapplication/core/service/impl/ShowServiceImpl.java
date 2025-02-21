package com.baseapplication.core.service.impl;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baseapplication.core.dao.ShowDao;
import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.exception.ResourceNotFoundException;
import com.baseapplication.core.model.RepertorioEvento;
import com.baseapplication.core.model.Show;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.service.ShowService;

@Service
public class ShowServiceImpl implements ShowService {

	@Autowired
	private ShowDao showDao;

	@Override
	public Show buscarPorId(Long idShow) {
		return showDao.findById(idShow).orElseThrow(() -> new ResourceNotFoundException("Show (" + idShow + ") não encontrado"));
	}

	@Override
	public List<Show> buscarPendentesPorUsuarioOrdenadoPorData(Long idUsuario) {
		return showDao.buscarPorIdUsuario(idUsuario).stream().filter(i -> i.getStatus().equals(StatusEvento.PENDENTE))
				.sorted(Comparator.comparing(Show::getData)).collect(Collectors.toList());
	}

	@Override
	public void salvar(Show show) {
		showDao.save(show);
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

}
