package com.baseapplication.core.controller;

import com.baseapplication.core.dto.AudioMusicaDTO;
import com.baseapplication.core.dto.QuotaAudioDTO;
import com.baseapplication.core.service.AudioMusicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * VS das musicas da banda. O playback nao passa por aqui: o objeto e servido direto pelo
 * MinIO, que fala HTTP Range e deixa o {@code <audio>} buscar no meio da faixa sem carregar
 * o arquivo inteiro. Esta API so cadastra e remove.
 */
@RestController
@RequestMapping("/audios-musica")
@RequiredArgsConstructor
public class AudioMusicaController {

    private final AudioMusicaService audioMusicaService;

    @GetMapping("/banda/{idBanda}")
    public ResponseEntity<List<AudioMusicaDTO>> listar(@PathVariable Long idBanda) {
        return ResponseEntity.ok(audioMusicaService.listarPorBanda(idBanda));
    }

    @GetMapping("/banda/{idBanda}/quota")
    public ResponseEntity<QuotaAudioDTO> quota(@PathVariable Long idBanda) {
        return ResponseEntity.ok(audioMusicaService.consultarQuota(idBanda));
    }

    @PostMapping("/banda/{idBanda}")
    public ResponseEntity<AudioMusicaDTO> enviar(@PathVariable Long idBanda,
                                                 @RequestParam String titulo,
                                                 @RequestParam(required = false) String artista,
                                                 @RequestParam("arquivo") MultipartFile arquivo) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(audioMusicaService.enviar(idBanda, titulo, artista, arquivo));
    }

    @DeleteMapping("/banda/{idBanda}/{idAudio}")
    public ResponseEntity<Void> remover(@PathVariable Long idBanda, @PathVariable Long idAudio) {
        audioMusicaService.remover(idBanda, idAudio);
        return ResponseEntity.noContent().build();
    }
}
