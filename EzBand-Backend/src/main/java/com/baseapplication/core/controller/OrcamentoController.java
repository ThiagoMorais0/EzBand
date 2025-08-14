package com.baseapplication.core.controller;

import com.baseapplication.core.dto.NovoOrcamentoDTO;
import com.baseapplication.core.model.dto.ParametroCustoDTO;
import com.baseapplication.core.service.OrcamentoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orcamento")
public class OrcamentoController {

    private final OrcamentoService orcamentoService;

    @GetMapping("/buscarParametrosCusto")
    public ResponseEntity<?> buscarParametrosCusto(@RequestParam Long idBanda){
        try{
            return ResponseEntity.ok(orcamentoService.buscarParametrosCusto(idBanda));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/salvarParametrosCusto")
    public ResponseEntity<?> salvarParametrosCusto(@RequestBody List<ParametroCustoDTO> dto){
        try{
            orcamentoService.salvarParametrosCusto(dto);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/deletarParametroCusto")
    public ResponseEntity<?> deletarParametroCusto(@RequestBody ParametroCustoDTO dto){
        try{
            orcamentoService.deletarParametroCusto(dto);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/gerarOrcamento")
    public ResponseEntity<?> gerarOrcamento(@RequestBody NovoOrcamentoDTO novoOrcamento){
        try{
            orcamentoService.gerarOrcamento(novoOrcamento);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }




}
