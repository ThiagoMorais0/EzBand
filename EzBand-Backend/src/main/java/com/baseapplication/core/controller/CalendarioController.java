package com.baseapplication.core.controller;

import com.baseapplication.core.dao.CompromissoPessoalDao;
import com.baseapplication.core.dao.EnsaioDao;
import com.baseapplication.core.dao.MusicoBandaDao;
import com.baseapplication.core.dao.ShowDao;
import com.baseapplication.core.dto.CalendarioEventoDTO;
import com.baseapplication.core.dto.CalendarioResponseDTO;
import com.baseapplication.core.dto.CompromissoPessoalDTO;
import com.baseapplication.core.dto.SalvarCompromissosDTO;
import com.baseapplication.core.model.CompromissoPessoal;
import com.baseapplication.core.model.Ensaio;
import com.baseapplication.core.model.MusicoBanda;
import com.baseapplication.core.model.MusicoBandaId;
import com.baseapplication.core.model.Show;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.utils.Context;
import com.baseapplication.core.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/calendario")
public class CalendarioController {

    @Autowired
    private ShowDao showDao;

    @Autowired
    private EnsaioDao ensaioDao;

    @Autowired
    private CompromissoPessoalDao compromissoPessoalDao;

    @Autowired
    private MusicoBandaDao musicoBandaDao;

    @GetMapping("/buscarEventosPorMes")
    public ResponseEntity<?> buscarEventosPorMes(@RequestParam int ano, @RequestParam int mes) {
        try {
            Usuario usuario = Context.getUsuarioLogado();
            Long idUsuario = usuario.getId();

            YearMonth yearMonth = YearMonth.of(ano, mes);
            LocalDate inicio = yearMonth.atDay(1);
            LocalDate fim = yearMonth.atEndOfMonth();

            List<Show> shows = showDao.buscarPorUsuarioEPeriodo(idUsuario, inicio, fim);
            List<Ensaio> ensaios = ensaioDao.buscarPorUsuarioEPeriodo(idUsuario, inicio, fim);
            List<CompromissoPessoal> compromissos = compromissoPessoalDao.findByUsuarioIdAndDataBetween(idUsuario, inicio, fim);

            List<CalendarioEventoDTO> eventos = new ArrayList<>();

            for (Show show : shows) {
                String corHex = resolverCorHex(idUsuario, show.getBanda().getId());
                eventos.add(new CalendarioEventoDTO(show, corHex));
            }

            for (Ensaio ensaio : ensaios) {
                String corHex = resolverCorHex(idUsuario, ensaio.getBanda().getId());
                eventos.add(new CalendarioEventoDTO(ensaio, corHex));
            }

            List<CompromissoPessoalDTO> compromissosDTO = compromissos.stream()
                    .map(CompromissoPessoalDTO::new).toList();

            return ResponseEntity.ok(new CalendarioResponseDTO(eventos, compromissosDTO));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/compromisso/salvar")
    public ResponseEntity<?> salvarCompromissos(@RequestBody SalvarCompromissosDTO dto) {
        try {
            Usuario usuario = Context.getUsuarioLogado();
            List<CompromissoPessoalDTO> salvos = new ArrayList<>();

            for (String dataStr : dto.getDatas()) {
                LocalDate data = DateUtils.stringToLocalDate(dataStr);
                CompromissoPessoal compromisso = new CompromissoPessoal();
                compromisso.setUsuario(usuario);
                compromisso.setData(data);
                compromisso.setDescricao(dto.getDescricao());
                CompromissoPessoal saved = compromissoPessoalDao.save(compromisso);
                salvos.add(new CompromissoPessoalDTO(saved));
            }

            return ResponseEntity.ok(salvos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @DeleteMapping("/compromisso/remover")
    public ResponseEntity<?> removerCompromisso(@RequestParam Long id) {
        try {
            Usuario usuario = Context.getUsuarioLogado();
            CompromissoPessoal compromisso = compromissoPessoalDao.findById(id)
                    .orElseThrow(() -> new RuntimeException("Compromisso não encontrado"));

            if (!compromisso.getUsuario().getId().equals(usuario.getId())) {
                return ResponseEntity.status(403).body("Sem permissão");
            }

            compromissoPessoalDao.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    private String resolverCorHex(Long idUsuario, Long idBanda) {
        return musicoBandaDao.findById(new MusicoBandaId(idUsuario, idBanda))
                .map(MusicoBanda::getCorHex)
                .orElse(null);
    }
}
