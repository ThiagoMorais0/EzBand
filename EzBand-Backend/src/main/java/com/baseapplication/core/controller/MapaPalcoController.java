package com.baseapplication.core.controller;

import com.baseapplication.core.dto.palco.*;
import com.baseapplication.core.service.MapaPalcoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mapa-palco")
@RequiredArgsConstructor
public class MapaPalcoController {

    private final MapaPalcoService mapaPalcoService;

    // --- Catalogos (nao dependem de banda) ---

    @GetMapping("/templates")
    public ResponseEntity<List<TemplateMapaPalcoDTO>> listarTemplates() {
        return ResponseEntity.ok(mapaPalcoService.listarTemplates());
    }

    @GetMapping("/tipos-item")
    public ResponseEntity<List<TipoItemPalcoDTO>> listarTiposItem() {
        return ResponseEntity.ok(mapaPalcoService.listarTiposItem());
    }

    // --- Mapas ---

    @GetMapping("/banda/{idBanda}")
    public ResponseEntity<List<MapaPalcoResumoDTO>> listar(@PathVariable Long idBanda) {
        return ResponseEntity.ok(mapaPalcoService.listarPorBanda(idBanda));
    }

    @PostMapping("/banda/{idBanda}")
    public ResponseEntity<MapaPalcoDTO> criar(@PathVariable Long idBanda,
                                              @RequestBody CadastroMapaPalcoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapaPalcoService.criar(idBanda, dto));
    }

    /** Agregado completo. E a unica chamada que o editor faz para carregar a tela. */
    @GetMapping("/{id}")
    public ResponseEntity<MapaPalcoDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(mapaPalcoService.buscarAgregado(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MapaPalcoDTO> atualizar(@PathVariable Long id,
                                                  @RequestBody AtualizacaoMapaPalcoDTO dto) {
        return ResponseEntity.ok(mapaPalcoService.atualizar(id, dto));
    }

    @PostMapping("/{id}/duplicar")
    public ResponseEntity<MapaPalcoDTO> duplicar(@PathVariable Long id,
                                                 @RequestParam(required = false) String nome) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapaPalcoService.duplicar(id, nome));
    }

    @PutMapping("/{id}/padrao")
    public ResponseEntity<MapaPalcoDTO> definirPadrao(@PathVariable Long id) {
        return ResponseEntity.ok(mapaPalcoService.definirPadrao(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        mapaPalcoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // --- Posicoes ---

    @PostMapping("/{id}/posicao")
    public ResponseEntity<PosicaoPalcoDTO> criarPosicao(@PathVariable Long id,
                                                        @RequestBody CadastroPosicaoPalcoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapaPalcoService.criarPosicao(id, dto));
    }

    @PutMapping("/posicao/{idPosicao}")
    public ResponseEntity<PosicaoPalcoDTO> atualizarPosicao(@PathVariable Long idPosicao,
                                                            @RequestBody CadastroPosicaoPalcoDTO dto) {
        return ResponseEntity.ok(mapaPalcoService.atualizarPosicao(idPosicao, dto));
    }

    @DeleteMapping("/posicao/{idPosicao}")
    public ResponseEntity<Void> deletarPosicao(@PathVariable Long idPosicao) {
        mapaPalcoService.deletarPosicao(idPosicao);
        return ResponseEntity.noContent().build();
    }

    /** Devolve o agregado inteiro para o editor atualizar os badges de resumo. */
    @PutMapping("/posicao/{idPosicao}/ficha")
    public ResponseEntity<MapaPalcoDTO> salvarFicha(@PathVariable Long idPosicao,
                                                    @RequestBody FichaPosicaoDTO dto) {
        return ResponseEntity.ok(mapaPalcoService.salvarFicha(idPosicao, dto));
    }

    // --- Vínculo com show ---

    /** idMapa ausente desvincula: o show volta a usar o mapa padrão da banda. */
    @PutMapping("/show/{idShow}")
    public ResponseEntity<Void> vincularAoShow(@PathVariable Long idShow,
                                               @RequestParam(required = false) Long idMapa) {
        mapaPalcoService.vincularAoShow(idShow, idMapa);
        return ResponseEntity.noContent().build();
    }

    // --- Rider ---

    @GetMapping("/{id}/rider")
    public ResponseEntity<RiderTecnicoDTO> gerarRider(@PathVariable Long id) {
        return ResponseEntity.ok(mapaPalcoService.gerarRider(id));
    }
}
