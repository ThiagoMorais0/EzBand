package com.baseapplication.core.controller;

import com.baseapplication.core.dto.AvaliacaoDTO;
import com.baseapplication.core.enums.TipoAvaliacao;
import com.baseapplication.core.service.AvaliacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/avaliacao")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    @PostMapping("/avaliar")
    public ResponseEntity<?> avaliar(@RequestBody AvaliacaoDTO avaliacao){
        try{
            avaliacaoService.avaliar(avaliacao);
            return ResponseEntity.ok().build();
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscarMediaAvaliacao")
    public ResponseEntity<?> buscarMediaAvaliacao(@RequestParam Long id, @RequestParam TipoAvaliacao tipo){
        try{
            return ResponseEntity.ok(avaliacaoService.buscarMedia(id, tipo));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
