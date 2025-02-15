package com.baseapplication.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.baseapplication.core.dto.CadastroBandaDTO;
import com.baseapplication.core.dto.ConviteEventoDTO;
import com.baseapplication.core.dto.RepertorioBandaDTO;
import com.baseapplication.core.dto.RetornoDTO;
import com.baseapplication.core.service.BandaService;
import com.baseapplication.core.utils.Context;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500")
@RequestMapping("/banda")
public class BandaController {

    @Autowired
    private BandaService bandaService;

    @GetMapping("/getInfo")
    public RetornoDTO getInfo(@RequestParam Long idBanda){
        try{
            return RetornoDTO.success(bandaService.getInfo(idBanda));
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

    @GetMapping("/buscarBandaParaIngressar")
    public RetornoDTO buscarBandaParaIngressar(@RequestParam Long idBanda){
        try{
            return RetornoDTO.success(bandaService.buscarBandaParaIngressar(idBanda));
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

    @PostMapping("/novaBanda")
    public RetornoDTO novaBanda(@RequestParam("banda") String bandaJson, MultipartFile logo) throws JsonProcessingException{
        try{
            CadastroBandaDTO bandaDTO = new ObjectMapper().readValue(bandaJson, CadastroBandaDTO.class);
            bandaService.novaBanda(bandaDTO, logo);
            return RetornoDTO.success();
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

    @PostMapping("/cadastrarUsuario")
    public RetornoDTO cadastrarUsuario(@RequestParam Long idBanda,
                                       @RequestParam Long idUsuario,
                                       @RequestParam String instrumentos){
        try{
            bandaService.cadastrarUsuario(idBanda, idUsuario, instrumentos);
            return RetornoDTO.success();
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

    @PostMapping("/expulsarUsuario")
    public RetornoDTO cadastrarUsuario(@RequestParam Long idBanda, @RequestParam Long idUsuario){
        try{
            bandaService.expulsarUsuario(idBanda, idUsuario);
            return RetornoDTO.success();
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

    @GetMapping("/getMembros")
    public RetornoDTO getMembros(@RequestParam Long idBanda){
        try{
            return RetornoDTO.success(bandaService.buscarMembros(idBanda));
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

    @PostMapping("/enviarConviteParaBanda")
    public RetornoDTO enviarConviteParaBanda(@RequestBody ConviteEventoDTO conviteEvento){
        try{
            //eventoService.enviarConviteParaEvento(conviteEvento);
            return RetornoDTO.success();
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

    @GetMapping("/buscarQuantidadeDeShows")
    public RetornoDTO buscarQuantidadeDeShows(@RequestParam Long idBanda){
        try{
            return RetornoDTO.success(bandaService.buscarQuantidadeDeShows(idBanda));
        }catch (Exception e){
            return RetornoDTO.error("Erro ao buscar quantidade de shows");
        }
    }

    @GetMapping("/buscarQuantidadeDeEnsaios")
    public RetornoDTO buscarQuantidadeDeEnsaios(@RequestParam Long idBanda){
        try{
            return RetornoDTO.success(bandaService.buscarQuantidadeDeEnsaios(idBanda));
        }catch (Exception e){
            return RetornoDTO.error("Erro ao buscar quantidade de shows");
        }
    }

    @GetMapping("/buscarQuantidadeDeNotificacoes")
    public RetornoDTO buscarQuantidadeDeNotificacoes(@RequestParam Long idBanda){
        try{
            return RetornoDTO.success(bandaService.buscarQuantidadeDeNotificacoes(idBanda));
        }catch (Exception e){
            return RetornoDTO.error("Erro ao buscar quantidade de shows");
        }
    }

    @GetMapping("/buscarQuantidadeDeMembros")
    public RetornoDTO buscarQuantidadeDeMembros(@RequestParam Long idBanda){
        try{
            return RetornoDTO.success(bandaService.buscarQuantidadeDeMembros(idBanda));
        }catch (Exception e){
            return RetornoDTO.error("Erro ao buscar quantidade de shows");
        }
    }

    @GetMapping("/buscarQuantidadeDeMusicasNoRepertorio")
    public RetornoDTO buscarQuantidadeDeMusicasNoRepertorio(@RequestParam Long idBanda){
        try{
            return RetornoDTO.success(bandaService.buscarQuantidadeDeMusicasNoRepertorio(idBanda));
        }catch (Exception e){
            return RetornoDTO.error("Erro ao buscar quantidade de shows");
        }
    }

    @GetMapping("/getNivelPermissaoUsuario")
    public RetornoDTO getNivelPermissaoUsuario(@RequestParam Long idBanda){
        try{
            return RetornoDTO.success(bandaService.getNivelPermissaoUsuario(idBanda, Context.getUsuarioLogado().getId()));
        }catch (Exception e){
            return RetornoDTO.error("Erro ao buscar quantidade de shows");
        }
    }

    @PostMapping("/sairDaBanda")
    public RetornoDTO sairDaBanda(@RequestParam Long idBanda){
        try{
            bandaService.sairDaBanda(idBanda, Context.getUsuarioLogado().getId());
            return RetornoDTO.success();
        }catch (Exception e){
            return RetornoDTO.error("Erro ao sair da banda");
        }
    }

    @GetMapping("/buscarShowsFuturosBanda")
    public RetornoDTO buscarShowsFuturosBanda(@RequestParam Long idBanda){
        try{
            return RetornoDTO.success(bandaService.buscarShowsFuturosBanda(idBanda, Context.getUsuarioLogado().getId()));
        }catch (Exception e){
            return RetornoDTO.error("Erro ao buscar shows futuros");
        }
    }

    @GetMapping("/buscarEnsaiosFuturosBanda")
    public RetornoDTO buscarEnsaiosFuturosBanda(@RequestParam Long idBanda){
        try{
            return RetornoDTO.success(bandaService.buscarEnsaiosFuturosBanda(idBanda, Context.getUsuarioLogado().getId()));
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

    @GetMapping("/buscarRepertorio")
    public RetornoDTO buscarRepertorio(@RequestParam Long idBanda){
        try{
            return RetornoDTO.success(bandaService.buscarRepertorio(idBanda));
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

    @PostMapping("/adicionarMusicaAoRepertorio")
    public RetornoDTO adicionarMusicaAoRepertorio(@RequestBody RepertorioBandaDTO repertorioBandaDTO){
        try{
            bandaService.adicionarMusicaAoRepertorio(repertorioBandaDTO);
            return RetornoDTO.success();
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }
}
