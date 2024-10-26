package com.baseapplication.core.controller;

import com.baseapplication.core.dto.AtualizacaoRepertorioEventoDTO;
import com.baseapplication.core.dto.ConviteEventoDTO;
import com.baseapplication.core.dto.RetornoDTO;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.service.BandaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("atualizarRepertorioEvento")
    public RetornoDTO atualizarRepertorioEvento(@RequestBody AtualizacaoRepertorioEventoDTO atualizacaoRepertorio){
        try{
            bandaService.atualizarRepertorioEvento(atualizacaoRepertorio);
            return RetornoDTO.success();
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

    @GetMapping("buscarRepertorioEvento")
    public RetornoDTO buscarRepertorioEvento(@RequestParam Long idEvento, @RequestParam TipoEvento tipoEvento){
        try{
            return RetornoDTO.success(bandaService.buscarRepertorioEvento(idEvento, tipoEvento));
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


}
