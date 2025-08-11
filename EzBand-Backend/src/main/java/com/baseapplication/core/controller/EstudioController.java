package com.baseapplication.core.controller;

import com.baseapplication.core.dto.CadastroEstudioDTO;
import com.baseapplication.core.dto.EstudioDTO;
import com.baseapplication.core.service.EstudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            return ResponseEntity.ok(service.buscarEstudiosDoUsuario().stream().map(EstudioDTO::new).toList());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscarTodos")
    public List<EstudioDTO> buscarTodos(){
        return service.buscarTodos().stream().map(EstudioDTO::new).toList();
    }

    @GetMapping("/buscarPorId")
    public ResponseEntity<?> buscarPorId(@RequestParam Long idEstudio){
        try{
            return ResponseEntity.ok(new EstudioDTO(service.buscarPorId(idEstudio)));
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
}
