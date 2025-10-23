package com.baseapplication.core.controller;

import com.baseapplication.core.dto.NovaPublicacaoDTO;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.service.PublicacaoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/publicacao")
@RequiredArgsConstructor
public class PublicacaoController {

    private final PublicacaoService publicacaoService;

    @PostMapping("/publicar")
    public ResponseEntity<String> publicarPublicacao(@RequestParam String publicacaoJson, List<MultipartFile> imagens) {

        NovaPublicacaoDTO publicacao = null;
        try {
            publicacao = new ObjectMapper().readValue(publicacaoJson, NovaPublicacaoDTO.class);
        } catch (JsonProcessingException e) {
            throw new InternalException(e.getMessage());
        }

        publicacaoService.publicar(publicacao, imagens);
        return ResponseEntity.ok("Publicacao publicada com sucesso");
    }

    @PostMapping("/editarTexto")
    public ResponseEntity<String> editarTextoPublicacao(@RequestParam Long idPublicacao, @RequestParam String texto) {
        try {
            publicacaoService.editarTextoPublicacao(idPublicacao, texto);
            return ResponseEntity.ok("Texto editado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/excluirPublicacao")
    public ResponseEntity<String> excluirPublicacao(@RequestParam Long idPublicacao) {
        try {
            publicacaoService.excluirPublicacao(idPublicacao);
            return ResponseEntity.ok("Publicacao excluida com sucesso");
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
