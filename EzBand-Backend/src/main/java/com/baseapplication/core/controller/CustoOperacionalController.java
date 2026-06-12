package com.baseapplication.core.controller;

import com.baseapplication.core.dto.CustoOperacionalDTO;
import com.baseapplication.core.dto.CustoOperacionalRequestDTO;
import com.baseapplication.core.service.CustoOperacionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/show/custoOperacional")
@RequiredArgsConstructor
public class CustoOperacionalController {

    private final CustoOperacionalService custoOperacionalService;

    @GetMapping("/listar")
    public ResponseEntity<List<CustoOperacionalDTO>> listar(@RequestParam Long idShow) {
        return ResponseEntity.ok(custoOperacionalService.buscarPorShow(idShow));
    }

    @PostMapping("/adicionar")
    public ResponseEntity<CustoOperacionalDTO> adicionar(@RequestBody CustoOperacionalRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(custoOperacionalService.adicionar(dto));
    }

    @PostMapping("/atualizar")
    public ResponseEntity<CustoOperacionalDTO> atualizar(@RequestParam Long id, @RequestBody CustoOperacionalRequestDTO dto) {
        return ResponseEntity.ok(custoOperacionalService.atualizar(id, dto));
    }

    @PostMapping("/remover")
    public ResponseEntity<Void> remover(@RequestParam Long id) {
        custoOperacionalService.remover(id);
        return ResponseEntity.ok().build();
    }
}
