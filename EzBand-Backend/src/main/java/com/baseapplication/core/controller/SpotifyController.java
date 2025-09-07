package com.baseapplication.core.controller;

import com.baseapplication.core.service.SpotifyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spotify")
@RequiredArgsConstructor
public class SpotifyController {

    private final SpotifyService spotifyService;

    @PostMapping("/adicinarMusicasDaPlaylistAoRepertorioDaBanda")
    public ResponseEntity<?> getPlaylistInfo(@RequestParam String url, @RequestParam Long idBanda) {
        try {
            return ResponseEntity.ok(spotifyService.adicionarMusicasDaPlaylistAoRepertorioDaBanda(url, idBanda));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar dados da playlist.");
        }
    }


}
