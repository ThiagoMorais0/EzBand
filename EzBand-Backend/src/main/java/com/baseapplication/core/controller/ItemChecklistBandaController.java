package com.baseapplication.core.controller;

import com.baseapplication.core.dto.CadastroItemChecklistBandaDTO;
import com.baseapplication.core.dto.ItemChecklistBandaDTO;
import com.baseapplication.core.service.ItemChecklistBandaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RequestMapping("/checklist/banda")
@RequiredArgsConstructor
public class ItemChecklistBandaController {
    
    private final ItemChecklistBandaService itemChecklistBandaService;
    
    @GetMapping("/listar")
    public ResponseEntity<List<ItemChecklistBandaDTO>> listar(@RequestParam Long idBanda) {
        List<ItemChecklistBandaDTO> itens = itemChecklistBandaService.buscarPorIdBanda(idBanda);
        return ResponseEntity.ok(itens);
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<ItemChecklistBandaDTO> buscar(@RequestParam Long id) {
        ItemChecklistBandaDTO item = itemChecklistBandaService.buscarPorId(id);
        return ResponseEntity.ok(item);
    }
    
    @PostMapping("/cadastrar")
    public ResponseEntity<ItemChecklistBandaDTO> cadastrar(@RequestBody CadastroItemChecklistBandaDTO dto) {
        ItemChecklistBandaDTO item = itemChecklistBandaService.cadastrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }
    
    @PostMapping("/atualizar")
    public ResponseEntity<ItemChecklistBandaDTO> atualizar(@RequestParam Long id, @RequestBody CadastroItemChecklistBandaDTO dto) {
        ItemChecklistBandaDTO item = itemChecklistBandaService.atualizar(id, dto);
        return ResponseEntity.ok(item);
    }
    
    @PostMapping("/deletar")
    public ResponseEntity<Void> deletar(@RequestParam Long id) {
        itemChecklistBandaService.deletar(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/ativar-desativar")
    public ResponseEntity<Void> ativarDesativar(@RequestParam Long id, @RequestParam Boolean ativo) {
        itemChecklistBandaService.ativarDesativar(id, ativo);
        return ResponseEntity.ok().build();
    }
}
