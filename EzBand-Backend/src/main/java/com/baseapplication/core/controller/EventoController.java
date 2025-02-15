package com.baseapplication.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baseapplication.core.dto.AtualizacaoRepertorioEventoDTO;
import com.baseapplication.core.dto.ConviteEventoDTO;
import com.baseapplication.core.dto.NovoShowDTO;
import com.baseapplication.core.dto.RetornoDTO;
import com.baseapplication.core.dto.superClasses.InformacoesEventoDTO;
import com.baseapplication.core.enums.TipoContato;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.service.EventoService;
import com.baseapplication.core.utils.DateUtils;

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

    @GetMapping("buscarRepertorioEvento")
    public RetornoDTO buscarRepertorioEvento(@RequestParam Long idEvento, @RequestParam TipoEvento tipoEvento){
        try{
            return RetornoDTO.success(eventoService.buscarRepertorioEvento(idEvento, tipoEvento));
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

    @PostMapping("atualizarRepertorioEvento")
    public RetornoDTO atualizarRepertorioEvento(@RequestBody AtualizacaoRepertorioEventoDTO atualizacaoRepertorio){
        try{
            eventoService.atualizarRepertorioEvento(atualizacaoRepertorio);
            return RetornoDTO.success();
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

    @GetMapping("/buscarMembrosEDisponibilidadeParaShow")
    public RetornoDTO buscarMembrosParaShow(@RequestParam Long idBanda,
                                            @RequestParam String data){
        try{
            return RetornoDTO.success(
                    eventoService.buscarMembrosEDisponibilidadeParaShow(idBanda, DateUtils.stringToLocalDate(data))
            );
        } catch (Exception e){
            return RetornoDTO.error("Erro ao buscar membros para show");
        }
    }

    @PostMapping("/marcarShow")
    public RetornoDTO marcarShow(@RequestBody NovoShowDTO novoShowDTO){
        try{
            eventoService.marcarShow(novoShowDTO);
            return RetornoDTO.success();
        } catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

    @PostMapping("/marcarEnsaio")
    public RetornoDTO marcarEnsaio(@RequestBody NovoEnsaioDTO novoEnsaioDTO){
        try{
            eventoService.marcarEnsaio(novoEnsaioDTO);
            return RetornoDTO.success();
        } catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

    @GetMapping("/isNotificacaoShowAceitaPorTodosMembros")
    public RetornoDTO isNotificacaoShowAceitaPorTodosMembros(Long idShow){
        try{
            return RetornoDTO.success(eventoService.isNotificacaoShowAceitaPorTodosMembros(idShow));
        }catch (Exception e){
            return RetornoDTO.error(e.getMessage());
        }
    }

    @PostMapping("/aceitarNotificacao")
    public RetornoDTO aceitarNotificacao(@RequestParam Long idNotificacao){
        try{
            eventoService.aceitarNotificacao(idNotificacao);
            return RetornoDTO.success();
        }catch (Exception e){
            return RetornoDTO.error();
        }
    }

    @PostMapping("/recusarNotificacao")
    public RetornoDTO recusarNotificacao(@RequestParam Long idNotificacao){
        try{
            eventoService.recusarNotificacao(idNotificacao);
            return RetornoDTO.success();
        }catch (Exception e){
            return RetornoDTO.error();
        }
    }

}
