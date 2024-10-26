package com.baseapplication.core.controller;

import com.baseapplication.core.dto.ConviteEventoDTO;
import com.baseapplication.core.dto.RetornoDTO;
import com.baseapplication.core.dto.superClasses.InformacoesEventoDTO;
import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.service.EventoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/evento")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @GetMapping("/buscarPorId")
    public RetornoDTO buscar(@RequestParam Long idEvento, @RequestParam TipoEvento tipoEvento){
        try{
            return RetornoDTO.success(eventoService.buscarPorId(idEvento, tipoEvento));
        }catch (Exception e){
            return RetornoDTO.error();
        }
    }

    @GetMapping("/buscarInformacoesEvento")
    public RetornoDTO buscarInformacoesEvento(@RequestParam Long idEvento, @RequestParam TipoEvento tipoEvento){
        try{
            return RetornoDTO.success(eventoService.buscarPobuscarInformacoesEventorId(idEvento, tipoEvento));
        }catch (Exception e){
            return RetornoDTO.error();
        }
    }

    @PostMapping("/atualizarInformacoesEvento")
    public RetornoDTO atualizarInformacoesEvento(@RequestBody InformacoesEventoDTO informacoesEventoDTO){
        try{
            eventoService.atualizarInformacoesEvento(informacoesEventoDTO);
            return RetornoDTO.success();
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

    @GetMapping("/buscarMusicoParaEvento")
    public RetornoDTO buscarMusicoParaEvento(@RequestParam String contato, @RequestParam TipoContato tipoContato){
        try{
            return RetornoDTO.success(eventoService.buscarMusicoParaEvento(contato, tipoContato));
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

    @PostMapping("/enviarConviteParaEvento")
    public RetornoDTO enviarConviteParaEvento(@RequestBody ConviteEventoDTO conviteEvento){
        try{
            eventoService.enviarConviteParaEvento(conviteEvento);
            return RetornoDTO.success();
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

}
