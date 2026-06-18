package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.PreferenciaNotificacaoMembroDao;
import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.event.events.NovoEventoMarcadoEvent;
import com.baseapplication.core.model.*;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.service.NotificacaoEventoService;
import com.baseapplication.core.service.WhatsappService;
import com.baseapplication.core.utils.PhoneNumberUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificacaoEventoServiceImpl implements NotificacaoEventoService {

    private final PreferenciaNotificacaoMembroDao preferenciaDao;
    private final WhatsappService whatsappService;
    private final PhoneNumberUtil phoneNumberUtil;
    private final EntityManager entityManager;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Async
    public void notificarNovoEvento(Long idEvento, TipoEvento tipoEvento) {
        notificarNovoEvento(idEvento, tipoEvento, null);
    }

    @Override
    @Async
    public void notificarNovoEvento(Long idEvento, TipoEvento tipoEvento, Long idUsuarioCriador) {
        log.info("Notificando novo evento: {} - {}", idEvento, tipoEvento);

        Evento evento = buscarEvento(idEvento, tipoEvento);
        if (evento == null) {
            log.warn("Evento não encontrado: {} - {}", idEvento, tipoEvento);
            return;
        }

        Banda banda = evento.getBanda();
        List<PreferenciaNotificacaoMembro> preferencias = preferenciaDao.buscarPorBanda(banda.getId());

        // WhatsApp — mantém comportamento existente
        for (PreferenciaNotificacaoMembro preferencia : preferencias) {
            if (Boolean.TRUE.equals(preferencia.getNotificarNovoEvento())) {
                enviarNotificacaoNovoEvento(preferencia, evento, tipoEvento);
            }
        }

        // In-app + push — via evento, só para membros reais com preferência ativa, excluindo o criador
        if (idUsuarioCriador == null) return;

        Usuario criador = entityManager.find(Usuario.class, idUsuarioCriador);
        if (criador == null) {
            log.warn("Criador não encontrado: {}", idUsuarioCriador);
            return;
        }

        List<Long> destinatarios = preferencias.stream()
                .filter(p -> Boolean.TRUE.equals(p.getNotificarNovoEvento())
                        && p.getIdUsuario() != null
                        && !p.getIdUsuario().equals(idUsuarioCriador))
                .map(PreferenciaNotificacaoMembro::getIdUsuario)
                .collect(Collectors.toList());

        if (destinatarios.isEmpty()) return;

        String local = evento.getLocal() != null ? evento.getLocal() : "local a definir";

        applicationEventPublisher.publishEvent(new NovoEventoMarcadoEvent(
                idEvento,
                tipoEvento,
                banda.getId(),
                banda.getNome(),
                banda.getUrlLogo(),
                local,
                criador.getId(),
                criador.getNome(),
                destinatarios
        ));
    }

    @Override
    @Scheduled(cron = "0 0 9 * * *")
    public void verificarENotificarEventosProximos() {
        log.info("Verificando eventos próximos para notificação...");
        
        LocalDate hoje = LocalDate.now();
        
        List<PreferenciaNotificacaoMembro> todasPreferencias = preferenciaDao.findAll();
        
        for (PreferenciaNotificacaoMembro preferencia : todasPreferencias) {
            if (preferencia.getDiasAntecedenciaNotificacao() != null && 
                !preferencia.getDiasAntecedenciaNotificacao().isEmpty()) {
                
                for (Integer dias : preferencia.getDiasAntecedenciaNotificacao()) {
                    LocalDate dataEvento = hoje.plusDays(dias);
                    verificarEventosNaData(preferencia, dataEvento, dias);
                }
            }
        }
    }

    private void verificarEventosNaData(PreferenciaNotificacaoMembro preferencia, LocalDate dataEvento, Integer diasAntecedencia) {
        String jpql = "SELECT e FROM Evento e WHERE e.banda.id = :idBanda AND e.data = :dataEvento";
        
        List<Evento> eventos = entityManager.createQuery(jpql, Evento.class)
                .setParameter("idBanda", preferencia.getIdBanda())
                .setParameter("dataEvento", dataEvento)
                .getResultList();
        
        for (Evento evento : eventos) {
            enviarNotificacaoLembrete(preferencia, evento, diasAntecedencia);
        }
    }

    private void enviarNotificacaoNovoEvento(PreferenciaNotificacaoMembro preferencia, Evento evento, TipoEvento tipoEvento) {
        String celular = obterCelularValidado(preferencia);
        if (celular == null) {
            return;
        }
        
        String nomeEvento = tipoEvento == TipoEvento.SHOW ? "Show" : "Ensaio";
        String dataFormatada = evento.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        
        String mensagem = String.format(
            "🎵 *EzBand Manager*\n\n" +
            "📅 *Novo %s Marcado!*\n\n" +
            "Banda: *%s*\n" +
            "Data: *%s*\n" +
            "Local: %s\n\n" +
            "Acesse o app para mais detalhes!",
            nomeEvento,
            evento.getBanda().getNome(),
            dataFormatada,
            evento.getLocal() != null ? evento.getLocal() : "A definir"
        );
        
        try {
            whatsappService.enviarMensagem(celular, mensagem);
            log.info("Notificação de novo evento enviada para: {}", celular);
        } catch (Exception e) {
            log.error("Erro ao enviar notificação de novo evento: {}", e.getMessage());
        }
    }

    private void enviarNotificacaoLembrete(PreferenciaNotificacaoMembro preferencia, Evento evento, Integer diasAntecedencia) {
        String celular = obterCelularValidado(preferencia);
        if (celular == null) {
            return;
        }
        
        String nomeEvento = evento.getTipoEvento() == TipoEvento.SHOW ? "Show" : "Ensaio";
        String dataFormatada = evento.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        
        String mensagemDias = diasAntecedencia == 1 ? "amanhã" : "em " + diasAntecedencia + " dias";
        
        String mensagem = String.format(
            "🎵 *EzBand Manager*\n\n" +
            "⏰ *Lembrete de %s*\n\n" +
            "Você tem um %s %s!\n\n" +
            "Banda: *%s*\n" +
            "Data: *%s*\n" +
            "Local: %s\n\n" +
            "Não esqueça! 🎸",
            nomeEvento,
            nomeEvento.toLowerCase(),
            mensagemDias,
            evento.getBanda().getNome(),
            dataFormatada,
            evento.getLocal() != null ? evento.getLocal() : "A definir"
        );
        
        try {
            whatsappService.enviarMensagem(celular, mensagem);
            log.info("Lembrete de evento enviado para: {} ({} dias de antecedência)", celular, diasAntecedencia);
        } catch (Exception e) {
            log.error("Erro ao enviar lembrete de evento: {}", e.getMessage());
        }
    }

    private String obterCelularValidado(PreferenciaNotificacaoMembro preferencia) {
        if (preferencia.getIdMembroFantasma() != null) {
            MembroFantasma membro = entityManager.find(MembroFantasma.class, preferencia.getIdMembroFantasma());
            if (membro != null && Boolean.TRUE.equals(membro.getCelularValidado()) && membro.getCelular() != null) {
                return phoneNumberUtil.normalizeToE164(membro.getCelular());
            }
        } else if (preferencia.getIdUsuario() != null) {
            Usuario usuario = entityManager.find(Usuario.class, preferencia.getIdUsuario());
            if (usuario != null && usuario.getCelular() != null) {
                return phoneNumberUtil.normalizeToE164(usuario.getCelular());
            }
        }
        return null;
    }

    private Evento buscarEvento(Long idEvento, TipoEvento tipoEvento) {
        if (tipoEvento == TipoEvento.SHOW) {
            return entityManager.find(Show.class, idEvento);
        } else if (tipoEvento == TipoEvento.ENSAIO) {
            return entityManager.find(Ensaio.class, idEvento);
        }
        return null;
    }
}
