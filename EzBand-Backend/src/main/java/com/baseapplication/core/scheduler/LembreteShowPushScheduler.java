package com.baseapplication.core.scheduler;

import com.baseapplication.core.dao.ShowDao;
import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.model.MusicoEvento;
import com.baseapplication.core.model.Show;
import com.baseapplication.core.service.WebPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LembreteShowPushScheduler {

    private final ShowDao showDao;
    private final WebPushService webPushService;

    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm");

    @Scheduled(cron = "0 0 13 * * *")
    public void lembreteVespera() {
        LocalDate amanha = LocalDate.now().plusDays(1);
        log.info("[LembreteShowPush] Processando shows de amanhã: {}", amanha);
        List<Show> shows = showDao.buscarPorDataEStatus(amanha, StatusEvento.PENDENTE);
        for (Show show : shows) {
            notificarParticipantes(show, false);
        }
        log.info("[LembreteShowPush] {} show(s) processado(s) para amanhã.", shows.size());
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void lembreteNoDia() {
        LocalDate hoje = LocalDate.now();
        log.info("[LembreteShowPush] Processando shows de hoje: {}", hoje);
        List<Show> shows = showDao.buscarPorDataEStatus(hoje, StatusEvento.PENDENTE);
        for (Show show : shows) {
            notificarParticipantes(show, true);
        }
        log.info("[LembreteShowPush] {} show(s) processado(s) para hoje.", shows.size());
    }

    private void notificarParticipantes(Show show, boolean ehHoje) {
        if (show.getParticipantes() == null || show.getParticipantes().isEmpty()) return;
        String titulo = ehHoje ? "Show hoje!" : "Show amanhã";
        String mensagem = montarMensagem(show, ehHoje);
        for (MusicoEvento participante : show.getParticipantes()) {
            if (participante.getUsuario() == null) continue;
            Long usuarioId = participante.getUsuario().getId();
            try {
                webPushService.enviarDireto(usuarioId, titulo, mensagem);
                log.info("[LembreteShowPush] Push enviado para usuário {} - show {} (banda: {})",
                        usuarioId, show.getId(), show.getBanda() != null ? show.getBanda().getNome() : "?");
            } catch (Exception e) {
                log.warn("[LembreteShowPush] Falha ao enviar para usuário {}: {}", usuarioId, e.getMessage());
            }
        }
    }

    private String montarMensagem(Show show, boolean ehHoje) {
        String prefixo = ehHoje ? "Hoje você tem show" : "Amanhã você tem show";
        String lugar = resolverLugar(show);
        String banda = show.getBanda() != null ? show.getBanda().getNome() : "";

        StringBuilder sb = new StringBuilder(prefixo);

        if (show.getHorarioInicio() != null) {
            sb.append(" às ").append(show.getHorarioInicio().toLocalTime().format(FMT_HORA));
        }
        if (!lugar.isBlank()) {
            sb.append(" em ").append(lugar);
        }
        if (!banda.isBlank()) {
            sb.append(" com a banda ").append(banda);
        }
        sb.append(".");

        if (show.getHorarioPassagemSom() != null) {
            sb.append(" Passagem de som às ")
              .append(show.getHorarioPassagemSom().toLocalTime().format(FMT_HORA))
              .append(".");
        }

        return sb.toString();
    }

    private String resolverLugar(Show show) {
        if (show.getLocalEvento() != null
                && show.getLocalEvento().getNome() != null
                && !show.getLocalEvento().getNome().isBlank()) {
            return show.getLocalEvento().getNome();
        }
        if (show.getLocal() != null && !show.getLocal().isBlank()) {
            return show.getLocal();
        }
        if (show.getEndereco() != null
                && show.getEndereco().getCidade() != null
                && !show.getEndereco().getCidade().isBlank()) {
            return show.getEndereco().getCidade();
        }
        return "";
    }
}
