package com.baseapplication.core.controller;

import java.util.List;

import com.baseapplication.core.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baseapplication.core.model.dto.BandaDTO;
import com.baseapplication.core.service.BandaService;
import com.baseapplication.core.utils.Context;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RequestMapping("/banda")
public class BandaController {

    @Autowired
    private BandaService bandaService;

    @GetMapping("/getInfo")
    public BandaDTO getInfo(@RequestParam Long idBanda) {
        return bandaService.getInfo(idBanda);
    }

    @GetMapping("/buscarBandaParaIngressar")
    public ResponseEntity<?> buscarBandaParaIngressar(@RequestParam Long idBanda) {
        return bandaService.buscarBandaParaIngressar(idBanda);
    }

    @PostMapping("/novaBanda")
    public void novaBanda(@RequestParam("banda") String bandaJson, MultipartFile logo) {
        bandaService.novaBanda(bandaJson, logo);
    }

    @PostMapping("/cadastrarUsuario")
    public void cadastrarUsuario(@RequestParam Long idBanda, @RequestParam Long idUsuario,
                                 @RequestParam String instrumentos) {
        bandaService.cadastrarUsuario(idBanda, idUsuario, instrumentos);
    }

    @PostMapping("/expulsarUsuario")
    public void expulsarUsuario(@RequestParam Long idBanda, @RequestParam Long idUsuario) {
        bandaService.expulsarUsuario(idBanda, idUsuario);
    }

    @GetMapping("/getMembros")
    public List<InfoMembroBandaDTO> getMembros(@RequestParam Long idBanda) {
        return bandaService.buscarMembros(idBanda);
    }

    @PostMapping("/enviarConviteParaBanda")
    public void enviarConviteParaBanda(@RequestBody ConviteEventoDTO conviteEvento) {
        // eventoService.enviarConviteParaEvento(conviteEvento);
    }

    @GetMapping("/buscarQuantidadeDeShows")
    public Integer buscarQuantidadeDeShows(@RequestParam Long idBanda) {
        return bandaService.buscarQuantidadeDeShows(idBanda);
    }

    @GetMapping("/buscarQuantidadeDeEnsaios")
    public Integer buscarQuantidadeDeEnsaios(@RequestParam Long idBanda) {
        return bandaService.buscarQuantidadeDeEnsaios(idBanda);
    }

    @GetMapping("/buscarQuantidadeDeNotificacoes")
    public Integer buscarQuantidadeDeNotificacoes(@RequestParam Long idBanda) {

        return bandaService.buscarQuantidadeDeNotificacoes(idBanda);

    }

    @GetMapping("/buscarQuantidadeDeMembros")
    public Integer buscarQuantidadeDeMembros(@RequestParam Long idBanda) {

        return bandaService.buscarQuantidadeDeMembros(idBanda);

    }

    @GetMapping("/buscarQuantidadeDeMusicasNoRepertorio")
    public Integer buscarQuantidadeDeMusicasNoRepertorio(@RequestParam Long idBanda) {

        return bandaService.buscarQuantidadeDeMusicasNoRepertorio(idBanda);

    }

    @GetMapping("/getPermissoesMusico")
    public ResponseEntity<?> getPermissoesMusico(@RequestParam Long idBanda) {
        try {
            return ResponseEntity.ok(
                    bandaService.getPermissoesMusico(idBanda, Context.getUsuarioLogado().getId())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/sairDaBanda")
    public ResponseEntity<?> sairDaBanda(@RequestParam Long idBanda) {
        try{
            bandaService.sairDaBanda(idBanda, Context.getUsuarioLogado().getId());
            return ResponseEntity.ok("Saiu da banda com sucesso");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscarShowsFuturosBanda")
    public ShowsFuturosDTO buscarShowsFuturosBanda(@RequestParam Long idBanda) {
        return bandaService.buscarShowsFuturosBanda(idBanda, Context.getUsuarioLogado().getId());
    }

    @GetMapping("/buscarEnsaiosFuturosBanda")
    public EnsaiosFuturosDTO buscarEnsaiosFuturosBanda(@RequestParam Long idBanda) {
        return bandaService.buscarEnsaiosFuturosBanda(idBanda, Context.getUsuarioLogado().getId());
    }

    @GetMapping("/buscarEnsaios")
    public ResponseEntity<?> buscarEnsaios(@RequestParam Long idBanda) {
        try{
            return ResponseEntity.ok(bandaService.buscarEnsaios(idBanda));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscarShows")
    public ResponseEntity<?> buscarShows(@RequestParam Long idBanda) {
        try{
            return ResponseEntity.ok(bandaService.buscarShows(idBanda));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/buscarRepertorio")
    public List<RepertorioBandaDTO> buscarRepertorio(@RequestParam Long idBanda) {
        return bandaService.buscarRepertorio(idBanda);
    }

    @PostMapping("/adicionarMusicaAoRepertorio")
    public void adicionarMusicaAoRepertorio(@RequestBody RepertorioBandaDTO repertorioBandaDTO) {
        bandaService.adicionarMusicaAoRepertorio(repertorioBandaDTO);
    }

    @PostMapping("/atualizarMusicaRepertorio")
    public ResponseEntity<?> atualizarMusicaRertorio(@RequestBody RepertorioBandaDTO repertorioBandaDTO) {
        try{
            bandaService.atualizarMusicaRertorio(repertorioBandaDTO);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @PostMapping("/editarBanda")
    public ResponseEntity<?> editarBanda(
            @RequestParam("banda") String bandaJson,
            @RequestParam(required = false) MultipartFile imagem,
            @RequestParam Boolean removerLogo) {
        try{
            bandaService.editarBanda(bandaJson, imagem, removerLogo);
            return ResponseEntity.ok("Banda editada com sucesso");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @PostMapping("/enviarConviteParaUsuarioIngressarBanda")
    public ResponseEntity<?> enviarConviteParaUsuarioIngressarBanda(
            @RequestParam Long idBanda,
            @RequestParam Long idUsuarioConvidado){
        try{
            bandaService.enviarConviteParaUsuarioIngressarBanda(idBanda, idUsuarioConvidado);
            return ResponseEntity.ok("Convite enviado com sucesso");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @PostMapping("/alterarPermissaoMembro")
    public ResponseEntity<?> alterarPermissaoMembro(@RequestBody EditarMembroMusicoBandaDTO permissaoMusicoDTO){
        try{
            bandaService.alterarPermissaoMembro(permissaoMusicoDTO);
            return ResponseEntity.ok("Salvo com sucesso");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/buscarSugestoes")
    public ResponseEntity<?> buscarSugestoes(@RequestBody BuscaBandaDTO dto){
        try{
            return ResponseEntity.ok(bandaService.buscarSugestoes(dto).stream().map(BandaDTO::new).toList());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
