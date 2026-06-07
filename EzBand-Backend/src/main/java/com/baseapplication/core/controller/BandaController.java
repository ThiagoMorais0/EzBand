package com.baseapplication.core.controller;

import java.util.List;

import com.baseapplication.core.dto.*;
import com.baseapplication.core.service.MembroFantasmaService;
import com.baseapplication.core.service.SugestaoRepertorioService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/banda")
@Log4j2
public class BandaController {

    @Autowired
    private BandaService bandaService;

    @Autowired
    private MembroFantasmaService membroFantasmaService;

    @Autowired
    private SugestaoRepertorioService sugestaoRepertorioService;

    @GetMapping("/getInfo")
    public BandaDTO getInfo(@RequestParam Long idBanda) {
        return bandaService.getInfo(idBanda);
    }

    @GetMapping("/buscarBandaParaIngressar")
    public ResponseEntity<?> buscarBandaParaIngressar(@RequestParam Long idBanda) {
        return bandaService.buscarBandaParaIngressar(idBanda);
    }

    @PostMapping("/novaBanda")
    public ResponseEntity<?> novaBanda(
            @RequestParam("banda") String bandaJson,
            @RequestParam(required = false) MultipartFile logo,
            @RequestParam(required = false) MultipartFile banner) {
        try {
            Long idBanda = bandaService.novaBanda(bandaJson, logo, banner);
            return ResponseEntity.ok(idBanda);
        } catch (Exception e) {
            log.error("Erro ao criar banda", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao criar a banda. Verifique os dados e tente novamente.");
        }
    }

    @PostMapping("/cadastrarUsuario")
    public void cadastrarUsuario(@RequestParam Long idBanda, @RequestParam Long idUsuario,
                                 @RequestParam String instrumentos) {
        bandaService.cadastrarUsuario(idBanda, idUsuario, instrumentos);
    }

    @PostMapping("/expulsarUsuario")
    public ResponseEntity<?> expulsarUsuario(@RequestParam Long idBanda, @RequestParam Long idUsuario) {
        try{
            bandaService.expulsarUsuario(idBanda, idUsuario);
            return ResponseEntity.ok("Usuário expulso");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/buscarRepertorio")
    public List<RepertorioBandaDTO> buscarRepertorio(@RequestParam Long idBanda) {
        return bandaService.buscarRepertorio(idBanda);
    }

    @PostMapping("/adicionarMusicaAoRepertorio")
    public ResponseEntity<?> adicionarMusicaAoRepertorio(@RequestBody RepertorioBandaDTO repertorioBandaDTO) {
        try{
            bandaService.adicionarMusicaAoRepertorio(repertorioBandaDTO);
            return ResponseEntity.ok("Música adicionada ao repertório com sucesso");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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

    @PostMapping("/removerMusicaDoRepertorio")
    public ResponseEntity<?> removerMusicaDoRepertorio(@RequestBody RepertorioBandaDTO repertorioBandaDTO) {
        try{
            bandaService.removerMusicaDoRepertorio(repertorioBandaDTO.getId(), repertorioBandaDTO.getIdBanda());
            return ResponseEntity.ok("Música removida do repertório com sucesso");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



    @PostMapping("/editarBanda")
    public ResponseEntity<?> editarBanda(
            @RequestParam("banda") String bandaJson,
            @RequestParam(required = false) MultipartFile imagem,
            @RequestParam Boolean removerLogo,
            @RequestParam(required = false) MultipartFile banner,
            @RequestParam(required = false, defaultValue = "false") Boolean removerBanner) {
        try {
            bandaService.editarBanda(bandaJson, imagem, removerLogo, banner, removerBanner);
            return ResponseEntity.ok("Banda editada com sucesso");
        } catch (Exception e) {
            log.error("Erro ao editar banda", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao salvar as alterações da banda. Tente novamente.");
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

    @PostMapping("/criarMembroFantasma")
    public ResponseEntity<?> criarMembroFantasma(@RequestParam("membro") String membroJson, @RequestParam(required = false) MultipartFile foto){
        try{
            MembroFantasmaDTO membroFantasma = membroFantasmaService.criar(membroJson, foto);
            return ResponseEntity.ok(membroFantasma);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/editarMembroFantasma")
    public ResponseEntity<?> editarMembroFantasma(@RequestBody EdicaoMembroFantasmaDTO dto){
        try{
            MembroFantasmaDTO membroFantasma = membroFantasmaService.editar(dto);
            return ResponseEntity.ok(membroFantasma);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/deletarMembroFantasma")
    public ResponseEntity<?> deletarMembroFantasma(@RequestParam Long id){
        try{
            membroFantasmaService.deletar(id);
            return ResponseEntity.ok("Membro fantasma deletado com sucesso");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscarMembrosFantasma")
    public ResponseEntity<?> buscarMembrosFantasma(@RequestParam Long idBanda){
        try{
            return ResponseEntity.ok(membroFantasmaService.buscarPorIdBanda(idBanda));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/buscarMembroFantasmaPorId")
    public ResponseEntity<?> buscarMembroFantasmaPorId(@RequestParam Long id){
        try{
            return ResponseEntity.ok(membroFantasmaService.buscarPorId(id));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/sugerirRepertorio")
    public ResponseEntity<?> sugerirRepertorio(@RequestBody SugestaoRepertorioDTO dto){
        try{
            return ResponseEntity.ok(sugestaoRepertorioService.sugerirRepertorio(dto));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/atualizarOrdemRepertorio")
    public ResponseEntity<?> atualizarOrdemRepertorio(@RequestParam Long idBanda, @RequestBody List<RepertorioBandaDTO> repertorio) {
        try{
            bandaService.atualizarOrdemRepertorio(idBanda, repertorio);
            return ResponseEntity.ok("Ordem atualizada com sucesso");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
