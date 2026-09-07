package com.baseapplication.core.service;

import com.baseapplication.core.dto.ConviteExternoGeradoDTO;
import com.baseapplication.core.dto.ConviteExternoPreviewDTO;
import com.baseapplication.core.dto.EventoConviteDTO;
import com.baseapplication.core.dto.EventoPendenteConviteDTO;
import com.baseapplication.core.dto.GerarConviteExternoDTO;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.Usuario;

import java.util.List;

public interface ConviteBandaService {

    List<EventoPendenteConviteDTO> buscarEventosPendentes(Long idBanda);

    ConviteExternoGeradoDTO gerarConviteExterno(GerarConviteExternoDTO dto);

    ConviteExternoPreviewDTO previewConviteExterno(String token);

    /** Aceita um convite por token, seja ele de notificação (usuário já cadastrado) ou externo. */
    Long aceitarConvitePorToken(String token);

    /** Valida os eventos informados e devolve a representação persistida ("SHOW:12,ENSAIO:33"). */
    String serializarEventos(Long idBanda, List<EventoConviteDTO> eventos);

    void incluirUsuarioNosEventos(Usuario usuario, Banda banda, String eventosSerializados);

    /**
     * Se já existe um convite em aberto da banda para o usuário, apenas soma os eventos escolhidos
     * a ele (a notificação duplicada seria descartada). Devolve true quando havia convite em aberto.
     */
    boolean atualizarConvitePendente(Long idBanda, Long idUsuarioConvidado, String eventosSerializados);
}
