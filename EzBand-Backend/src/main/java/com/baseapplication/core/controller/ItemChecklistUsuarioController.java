package com.baseapplication.core.controller;

import com.baseapplication.core.dto.CadastroItemChecklistUsuarioDTO;
import com.baseapplication.core.dto.ItemChecklistUsuarioDTO;
import com.baseapplication.core.service.ItemChecklistUsuarioService;
import com.baseapplication.core.utils.Context;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/checklist/usuario")
@RequiredArgsConstructor
public class ItemChecklistUsuarioController {
    
    private final ItemChecklistUsuarioService itemChecklistUsuarioService;
    
    @GetMapping("/listar")
    public ResponseEntity<List<ItemChecklistUsuarioDTO>> listar() {
        Long idUsuario = Context.getUsuarioLogado().getId();
        List<ItemChecklistUsuarioDTO> itens = itemChecklistUsuarioService.buscarPorIdUsuario(idUsuario);
        return ResponseEntity.ok(itens);
    }
    
    @GetMapping("/buscar")
    public ResponseEntity<ItemChecklistUsuarioDTO> buscar(@RequestParam Long id) {
        ItemChecklistUsuarioDTO item = itemChecklistUsuarioService.buscarPorId(id);
        return ResponseEntity.ok(item);
    }
    
    @PostMapping("/cadastrar")
    public ResponseEntity<ItemChecklistUsuarioDTO> cadastrar(@RequestBody CadastroItemChecklistUsuarioDTO dto) {
        Long idUsuario = Context.getUsuarioLogado().getId();
        ItemChecklistUsuarioDTO item = itemChecklistUsuarioService.cadastrar(dto, idUsuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }
    
    @PostMapping("/atualizar")
    public ResponseEntity<ItemChecklistUsuarioDTO> atualizar(@RequestParam Long id, @RequestBody CadastroItemChecklistUsuarioDTO dto) {
        ItemChecklistUsuarioDTO item = itemChecklistUsuarioService.atualizar(id, dto);
        return ResponseEntity.ok(item);
    }
    
    @PostMapping("/deletar")
    public ResponseEntity<Void> deletar(@RequestParam Long id) {
        itemChecklistUsuarioService.deletar(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/ativar-desativar")
    public ResponseEntity<Void> ativarDesativar(@RequestParam Long id, @RequestParam Boolean ativo) {
        itemChecklistUsuarioService.ativarDesativar(id, ativo);
        return ResponseEntity.ok().build();
    }
}
