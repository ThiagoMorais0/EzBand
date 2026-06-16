package com.baseapplication.core.scheduler;

import com.baseapplication.core.dao.ConfiguracaoNotificacaoUsuarioDao;
import com.baseapplication.core.dao.EnsaioDao;
import com.baseapplication.core.dao.ShowDao;
import com.baseapplication.core.dao.UsuarioDao;
import com.baseapplication.core.model.ConfiguracaoNotificacaoUsuario;
import com.baseapplication.core.model.Ensaio;
import com.baseapplication.core.model.Show;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.service.WhatsappService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LembreteEventoScheduler {

    private final ConfiguracaoNotificacaoUsuarioDao configuracaoDao;
    private final UsuarioDao usuarioDao;
    private final ShowDao showDao;
    private final EnsaioDao ensaioDao;
    private final WhatsappService whatsappService;

    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_HORA = DateTimeFormatter.ofPattern("HH:mm");

    @Scheduled(cron = "0 0 8 * * *")
    public void executar() {
        LocalDate hoje = LocalDate.now();
        log.info("[Scheduler] LembreteEventoScheduler rodando para {}", hoje);

        List<ConfiguracaoNotificacaoUsuario> configs = configuracaoDao.findByReceberNotificacoesWhatsappTrue();
        log.info("[Scheduler] {} usuário(s) com notificações WhatsApp habilitadas.", configs.size());

        for (ConfiguracaoNotificacaoUsuario config : configs) {
            try {
                processarUsuario(config, hoje);
            } catch (Exception e) {
                log.error("[Scheduler] Erro ao processar lembretes para usuário {}: {}", config.getIdUsuario(), e.getMessage(), e);
            }
        }
        log.info("[Scheduler] LembreteEventoScheduler concluído para {}", hoje);
    }

    private void processarUsuario(ConfiguracaoNotificacaoUsuario config, LocalDate hoje) {
        Long idUsuario = config.getIdUsuario();
        Optional<Usuario> usuarioOpt = usuarioDao.findById(idUsuario);
        if (usuarioOpt.isEmpty()) {
            log.warn("[Scheduler] Usuário {} da configuração não encontrado no banco.", idUsuario);
            return;
        }

        Usuario usuario = usuarioOpt.get();
        if (!Boolean.TRUE.equals(usuario.getCelularValidado())) {
            log.info("[Scheduler] Usuário {} pulado: celular não validado.", idUsuario);
            return;
        }
        if (usuario.getCelular() == null) {
            log.info("[Scheduler] Usuário {} pulado: celular não cadastrado.", idUsuario);
            return;
        }

        log.info("[Scheduler] Processando lembretes para usuário {} ({})", idUsuario, usuario.getCelular());
        enviarLembretes(config, usuario, hoje);
        verificarResumoSemanal(config, usuario, hoje);
    }

    private void enviarLembretes(ConfiguracaoNotificacaoUsuario config, Usuario usuario, LocalDate hoje) {
        if (config.getDiasAntecedenciaLembrete() == null || config.getDiasAntecedenciaLembrete().isEmpty()) {
            log.debug("[Scheduler] Usuário {} sem dias de antecedência configurados, pulando lembretes.", usuario.getId());
            return;
        }

        for (Integer dias : config.getDiasAntecedenciaLembrete()) {
            LocalDate dataAlvo = hoje.plusDays(dias);
            List<Evento> eventos = buscarEventosParaData(usuario.getId(), dataAlvo);
            log.info("[Scheduler] Usuário {}: {} evento(s) encontrado(s) para daqui a {} dia(s) ({})", usuario.getId(), eventos.size(), dias, dataAlvo);

            for (Evento evento : eventos) {
                String mensagem = montarMensagemLembrete(evento, dias);
                whatsappService.enviarMensagem(usuario.getCelular(), mensagem);
                log.info("[Scheduler] Lembrete enviado para usuário {} - evento {} em {}", usuario.getId(), evento.getId(), dataAlvo);
            }
        }
    }

    private void verificarResumoSemanal(ConfiguracaoNotificacaoUsuario config, Usuario usuario, LocalDate hoje) {
        if (config.getDiaSemanaResumoSemanal() == null) return;

        // DayOfWeek: MONDAY=1 ... SUNDAY=7
        int diaSemanaHoje = hoje.getDayOfWeek().getValue();
        if (diaSemanaHoje != config.getDiaSemanaResumoSemanal()) return;

        LocalDate inicioSemana = hoje;
        LocalDate fimSemana = hoje.plusDays(6);

        List<Show> shows = showDao.buscarPorUsuarioEPeriodo(usuario.getId(), inicioSemana, fimSemana);
        List<Ensaio> ensaios = ensaioDao.buscarPorUsuarioEPeriodo(usuario.getId(), inicioSemana, fimSemana);

        List<Evento> todos = new ArrayList<>();
        todos.addAll(shows);
        todos.addAll(ensaios);
        todos.sort((a, b) -> {
            int cmp = a.getData().compareTo(b.getData());
            if (cmp != 0) return cmp;
            if (a.getHorarioInicio() != null && b.getHorarioInicio() != null)
                return a.getHorarioInicio().compareTo(b.getHorarioInicio());
            return 0;
        });

        if (todos.isEmpty()) {
            log.info("[Scheduler] Usuário {}: nenhum evento na semana {}-{}, resumo semanal não enviado.", usuario.getId(), inicioSemana, fimSemana);
            return;
        }

        log.info("[Scheduler] Enviando resumo semanal para usuário {} com {} evento(s).", usuario.getId(), todos.size());
        String mensagem = montarMensagemResumoSemanal(todos, inicioSemana, fimSemana);
        whatsappService.enviarMensagem(usuario.getCelular(), mensagem);
        log.info("[Scheduler] Resumo semanal enviado para usuário {} com {} eventos", usuario.getId(), todos.size());
    }

    private List<Evento> buscarEventosParaData(Long idUsuario, LocalDate data) {
        List<Evento> eventos = new ArrayList<>();
        eventos.addAll(showDao.buscarPorUsuarioEPeriodo(idUsuario, data, data));
        eventos.addAll(ensaioDao.buscarPorUsuarioEPeriodo(idUsuario, data, data));
        return eventos;
    }

    private String montarMensagemLembrete(Evento evento, int diasAntecedencia) {
        String tipo = evento.getTipoEvento() != null ? evento.getTipoEvento().getDescricao() : "Compromisso";
        String banda = evento.getBanda() != null ? evento.getBanda().getNome() : "";
        String data = evento.getData() != null ? evento.getData().format(FMT_DATA) : "";
        String hora = evento.getHorarioInicio() != null
                ? evento.getHorarioInicio().toLocalTime().format(FMT_HORA)
                : "";
        String local = evento.getLocal() != null ? evento.getLocal() : "";

        String prefixo;
        if (diasAntecedencia == 0) {
            prefixo = "Hoje você tem um compromisso!";
        } else if (diasAntecedencia == 1) {
            prefixo = "Amanhã você tem um compromisso!";
        } else if (diasAntecedencia == 7) {
            prefixo = "Daqui a 1 semana você tem um compromisso!";
        } else {
            prefixo = "Daqui a " + diasAntecedencia + " dias você tem um compromisso!";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("*EzBand* - ").append(prefixo).append("\n\n");
        sb.append("*").append(tipo).append("*");
        if (!banda.isBlank()) sb.append(" com *").append(banda).append("*");
        sb.append("\n");
        if (!data.isBlank()) sb.append("📅 ").append(data);
        if (!hora.isBlank()) sb.append(" às ").append(hora);
        sb.append("\n");
        if (!local.isBlank()) sb.append("📍 ").append(local);
        return sb.toString().trim();
    }

    private String montarMensagemResumoSemanal(List<Evento> eventos, LocalDate inicio, LocalDate fim) {
        StringBuilder sb = new StringBuilder();
        sb.append("*EzBand - Seus compromissos desta semana*\n");
        sb.append("(").append(inicio.format(FMT_DATA)).append(" a ").append(fim.format(FMT_DATA)).append(")\n\n");

        for (Evento evento : eventos) {
            String tipo = evento.getTipoEvento() != null ? evento.getTipoEvento().getDescricao() : "Compromisso";
            String banda = evento.getBanda() != null ? evento.getBanda().getNome() : "";
            String data = evento.getData() != null ? evento.getData().format(FMT_DATA) : "";
            String diaSemana = evento.getData() != null
                    ? evento.getData().getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"))
                    : "";
            String hora = evento.getHorarioInicio() != null
                    ? evento.getHorarioInicio().toLocalTime().format(FMT_HORA)
                    : "";
            String local = evento.getLocal() != null ? evento.getLocal() : "";

            sb.append("• *").append(tipo).append("*");
            if (!banda.isBlank()) sb.append(" - ").append(banda);
            sb.append("\n");
            sb.append("  ").append(diaSemana).append(", ").append(data);
            if (!hora.isBlank()) sb.append(" às ").append(hora);
            if (!local.isBlank()) sb.append(" | ").append(local);
            sb.append("\n\n");
        }

        return sb.toString().trim();
    }
}
