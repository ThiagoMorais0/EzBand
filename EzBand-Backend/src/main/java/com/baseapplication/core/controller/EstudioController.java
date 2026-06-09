package com.baseapplication.core.controller;

import com.baseapplication.core.dto.*;
import com.baseapplication.core.dto.ServicoEstudioDTO;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.service.EstudioService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/estudio")
@RequiredArgsConstructor
public class EstudioController {

    private final EstudioService service;

    @PostMapping("/cadastrar")
    @ResponseBody
    public ResponseEntity<?> cadastrarEstudio(@RequestBody CadastroEstudioDTO cadastroEstudioDTO){
        try{
            service.cadastrar(cadastroEstudioDTO.toEntity());
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/editar")
    @ResponseBody
    public ResponseEntity<?> editar(@RequestBody EstudioDTO estudioDTO){
        try{
            service.editar(estudioDTO);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscarEstudiosDoUsuario")
    public ResponseEntity<?> buscarEstudiosDoUsuario(){
        try{
            return ResponseEntity.ok(service.buscarEstudiosDoUsuarioDTO());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/buscarTodos")
    public List<EstudioDTO> buscarTodos(){
        return service.buscarTodosDTO();
    }

    @GetMapping("/buscarPorId")
    public ResponseEntity<?> buscarPorId(@RequestParam Long idEstudio){
        try{
            return ResponseEntity.ok(service.buscarPorIdDTO(idEstudio));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/deletarTodos")
    public ResponseEntity<?> deletarTodos(){
        try{
            service.deletarTodos();
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/cadastrarComImagem")
    public ResponseEntity<?> cadastrarComImagem(@RequestParam("dados") String estudioJson,
                                                @RequestParam(value = "imagem", required = false) MultipartFile imagem) {
        CadastroEstudioDTO estudio;
        try {
            estudio = new ObjectMapper().readValue(estudioJson, CadastroEstudioDTO.class);
        } catch (JsonProcessingException e) {
            throw new InternalException(e.getMessage());
        }
        return service.cadastrarComImagemRetornandoId(estudio, imagem);
    }

    @PostMapping("/deletar")
    public ResponseEntity<?> deletar(@RequestParam Long idEstudio) {
        try {
            service.deletar(idEstudio);
            return ResponseEntity.ok("Estúdio deletado");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/adicionarSocio")
    public ResponseEntity<?> adicionarSocio(@RequestParam Long idEstudio, @RequestParam Long idUsuario) {
        try {
            return service.adicionarSocio(idEstudio, idUsuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/removerSocio")
    public ResponseEntity<?> removerSocio(@RequestParam Long idEstudio, @RequestParam Long idUsuario) {
        try {
            return service.removerSocio(idEstudio, idUsuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/editarComImagem")
    public ResponseEntity<?> editarComImagem(@RequestParam("usuario") String estudioJson,
                                             @RequestParam(value = "imagem", required = false) MultipartFile imagem) {
        try{
            service.editarComImagem(estudioJson, imagem);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscarEnsaiosPorEstudio")
    public ResponseEntity<?> buscarEnsaiosPorEstudio(@RequestParam Long idEstudio){
        try{
            return ResponseEntity.ok(service.buscarEnsaiosPorEstudio(idEstudio));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/adicionarServico")
    public ResponseEntity<?> adicionarServico(@RequestParam Long idEstudio,
                                              @RequestBody ServicoEstudioDTO dto) {
        try {
            return ResponseEntity.ok(service.adicionarServico(idEstudio, dto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/editarServico")
    public ResponseEntity<?> editarServico(@RequestBody ServicoEstudioDTO dto) {
        try {
            service.editarServico(dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/removerServico")
    public ResponseEntity<?> removerServico(@RequestParam Long idServico) {
        try {
            service.removerServico(idServico);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/adicionarEquipamento")
    public ResponseEntity<?> adicionarEquipamento(@RequestParam Long idEstudio,
                                                   @RequestParam("dados") String dadosJson,
                                                   @RequestParam(value = "imagem", required = false) MultipartFile imagem) {
        try {
            return ResponseEntity.ok(service.adicionarEquipamento(idEstudio, dadosJson, imagem));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/editarEquipamento")
    public ResponseEntity<?> editarEquipamento(@RequestParam Long idEquipamento,
                                                @RequestParam("dados") String dadosJson,
                                                @RequestParam(value = "imagem", required = false) MultipartFile imagem) {
        try {
            service.editarEquipamento(idEquipamento, dadosJson, imagem);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/removerEquipamento")
    public ResponseEntity<?> removerEquipamento(@RequestParam Long idEquipamento) {
        try {
            service.removerEquipamento(idEquipamento);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/buscarSugestoes")
    public ResponseEntity<?> buscarSugestoes(@RequestBody BuscaEstudioDTO dto ){
        try{
            return ResponseEntity.ok(service.buscarSugestoes(dto));
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
