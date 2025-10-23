package com.baseapplication.core.controller;

import com.baseapplication.core.dto.CadastroEstudioDTO;
import com.baseapplication.core.dto.CadastroLocalEventoDTO;
import com.baseapplication.core.dto.LocalEventoDTO;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.service.LocalEventoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/localEvento")
@RequiredArgsConstructor
public class LocalEventoController {

    private final LocalEventoService service;

    @PostMapping("/cadastrar")
    @ResponseBody
    public ResponseEntity<?> cadastrarLocalEvento(@RequestBody CadastroLocalEventoDTO cadastroLocalEventoDTO){
        try{
            service.cadastrar(cadastroLocalEventoDTO.toEntity());
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/cadastrarComImagem")
    @CrossOrigin(origins = "*")
    public ResponseEntity<?> cadastrarUsuarioComImagem(@RequestParam("dados") String localEventoJson, MultipartFile imagem) {

        CadastroLocalEventoDTO localEvento = null;
        try {
            localEvento = new ObjectMapper().readValue(localEventoJson, CadastroLocalEventoDTO.class);
        } catch (JsonProcessingException e) {
            throw new InternalException(e.getMessage());
        }

        return service.cadastrarComImagem(localEvento, imagem);
    }


    @PostMapping("/editar")
    @ResponseBody
    public ResponseEntity<?> editar(@RequestBody LocalEventoDTO localEventoDTO){
        try{
            service.editar(localEventoDTO);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscarLocalEventosDoUsuario")
    public ResponseEntity<?> buscarLocalEventosDoUsuario(){
        try{
            return ResponseEntity.ok(service.buscarLocalEventosDoUsuario().stream().map(LocalEventoDTO::new).toList());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscarTodos")
    public List<LocalEventoDTO> buscarTodos(){
        return service.buscarTodos().stream().map(LocalEventoDTO::new).toList();
    }

    @GetMapping("/buscarPorId")
    public ResponseEntity<?> buscarPorId(@RequestParam Long idLocalEvento){
        try{
            return ResponseEntity.ok(new LocalEventoDTO(service.buscarPorId(idLocalEvento)));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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

    @GetMapping("/buscarShowsPorLocalEvento")
    public ResponseEntity<?> buscarShowsPorLocalEvento(@RequestParam Long idLocalEvento){
        try{
            return ResponseEntity.ok(service.buscarShowsPorLocalEvento(idLocalEvento));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
