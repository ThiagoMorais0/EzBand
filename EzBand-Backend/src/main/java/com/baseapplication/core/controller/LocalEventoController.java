package com.baseapplication.core.controller;

import com.baseapplication.core.dto.BuscaLocalEventoDTO;
import com.baseapplication.core.dto.CadastroLocalEventoDTO;
import com.baseapplication.core.dto.LocalEventoDTO;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.service.LocalEventoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/localEvento")
@RequiredArgsConstructor
public class LocalEventoController {

    private final LocalEventoService service;

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrar(@RequestBody CadastroLocalEventoDTO dto) {
        try {
            service.cadastrar(dto.toEntity());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Erro em LocalEventoController: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/cadastrarComImagem")
    public ResponseEntity<?> cadastrarComImagem(@RequestParam("dados") String localEventoJson, MultipartFile imagem) {
        CadastroLocalEventoDTO dto;
        try {
            dto = new ObjectMapper().readValue(localEventoJson, CadastroLocalEventoDTO.class);
        } catch (JsonProcessingException e) {
            throw new InternalException(e.getMessage());
        }
        return service.cadastrarComImagem(dto, imagem);
    }

    @PostMapping("/editar")
    public ResponseEntity<?> editar(@RequestBody LocalEventoDTO dto) {
        try {
            service.editar(dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Erro em LocalEventoController: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/deletar")
    public ResponseEntity<?> deletar(@RequestParam Long idLocalEvento) {
        try {
            service.deletar(idLocalEvento);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Erro em LocalEventoController: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscarLocalEventosDoUsuario")
    public ResponseEntity<?> buscarLocalEventosDoUsuario() {
        try {
            return ResponseEntity.ok(service.buscarLocalEventosDoUsuario());
        } catch (Exception e) {
            log.error("Erro em LocalEventoController: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscarTodos")
    public List<LocalEventoDTO> buscarTodos() {
        return service.buscarTodos();
    }

    @GetMapping("/buscarPorId")
    public ResponseEntity<?> buscarPorId(@RequestParam Long idLocalEvento) {
        try {
            return ResponseEntity.ok(service.buscarPorId(idLocalEvento));
        } catch (Exception e) {
            log.error("Erro em LocalEventoController: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscarShowsPorLocalEvento")
    public ResponseEntity<?> buscarShowsPorLocalEvento(@RequestParam Long idLocalEvento) {
        try {
            return ResponseEntity.ok(service.buscarShowsPorLocalEvento(idLocalEvento));
        } catch (Exception e) {
            log.error("Erro em LocalEventoController: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscarShowsAguardandoAprovacao")
    public ResponseEntity<?> buscarShowsAguardandoAprovacao(@RequestParam Long idLocalEvento) {
        try {
            return ResponseEntity.ok(service.buscarShowsAguardandoAprovacao(idLocalEvento));
        } catch (Exception e) {
            log.error("Erro em LocalEventoController: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscarShowsHistorico")
    public ResponseEntity<?> buscarShowsHistorico(@RequestParam Long idLocalEvento) {
        try {
            return ResponseEntity.ok(service.buscarShowsHistorico(idLocalEvento));
        } catch (Exception e) {
            log.error("Erro em LocalEventoController: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/buscarSugestoes")
    public ResponseEntity<?> buscarSugestoes(@RequestBody BuscaLocalEventoDTO dto) {
        try {
            return ResponseEntity.ok(service.buscarSugestoes(dto));
        } catch (Exception e) {
            log.error("Erro em LocalEventoController: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/adicionarEquipamento")
    public ResponseEntity<?> adicionarEquipamento(@RequestParam Long idLocalEvento,
                                                   @RequestParam("dados") String dadosJson,
                                                   MultipartFile imagem) {
        try {
            return ResponseEntity.ok(service.adicionarEquipamento(idLocalEvento, dadosJson, imagem));
        } catch (Exception e) {
            log.error("Erro em LocalEventoController: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/editarEquipamento")
    public ResponseEntity<?> editarEquipamento(@RequestParam Long idEquipamento,
                                                @RequestParam("dados") String dadosJson,
                                                MultipartFile imagem) {
        try {
            service.editarEquipamento(idEquipamento, dadosJson, imagem);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Erro em LocalEventoController: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/removerEquipamento")
    public ResponseEntity<?> removerEquipamento(@RequestParam Long idEquipamento) {
        try {
            service.removerEquipamento(idEquipamento);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Erro em LocalEventoController: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/adicionarSocio")
    public ResponseEntity<?> adicionarSocio(@RequestParam Long idLocalEvento, @RequestParam Long idUsuario) {
        return service.adicionarSocio(idLocalEvento, idUsuario);
    }

    @PostMapping("/removerSocio")
    public ResponseEntity<?> removerSocio(@RequestParam Long idLocalEvento, @RequestParam Long idUsuario) {
        return service.removerSocio(idLocalEvento, idUsuario);
    }

    @PostMapping("/editarPermissaoSocio")
    public ResponseEntity<?> editarPermissaoSocio(@RequestParam Long idLocalEvento,
                                                   @RequestParam Long idUsuario,
                                                   @RequestParam Boolean aprovaShows) {
        return service.editarPermissaoSocio(idLocalEvento, idUsuario, aprovaShows);
    }

    @PostMapping("/aprovarShow")
    public ResponseEntity<?> aprovarShow(@RequestParam Long idShow) {
        return service.aprovarShow(idShow);
    }

    @PostMapping("/recusarShow")
    public ResponseEntity<?> recusarShow(@RequestParam Long idShow, @RequestParam String motivo) {
        return service.recusarShow(idShow, motivo);
    }

    @GetMapping("/deletarTodos")
    public ResponseEntity<?> deletarTodos() {
        try {
            service.deletarTodos();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Erro em LocalEventoController: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
