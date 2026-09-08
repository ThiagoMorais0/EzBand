package com.baseapplication.core.scheduler;

import com.baseapplication.core.dao.PendenciaAvaliacaoEventoDao;
import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.model.PendenciaAvaliacaoEvento;
import com.baseapplication.core.model.Show;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.service.EventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventoScheduler {
    private static final LocalTime FIM_DO_DIA = LocalTime.of(23, 59);

    private final EventoService eventoService;
    private final PendenciaAvaliacaoEventoDao pendenciaAvaliacaoEventoDao;

    /** Duração assumida para shows cadastrados sem duração. */
    @Value("${evento.duracao-padrao.show-horas:2}")
    private long duracaoPadraoShowHoras;

    @Scheduled(fixedRate = 60000 * 10)
    public void atualizarEventosConcluidos() {
        LocalDateTime agora = LocalDateTime.now();
        System.out.println("Rodando job, data/hora atual: " + agora);

        // busca eventos cuja data é menor ou igual a hoje e status ainda não é REALIZADO
        List<Evento> eventos = eventoService.buscarComDataAnteriorAHoje();

        for (Evento evento : eventos) {

            LocalDateTime termino = calcularTermino(evento);
            if (termino.isAfter(agora))
                continue;

            evento.setStatus(StatusEvento.REALIZADO);
            System.out.println("Evento concluído: " + evento.getBanda().getNome() + " em " + evento.getData() +
                    " (término " + termino + ") no " + evento.getLocal());
            eventoService.salvar(evento);

            // Cria pendência de avaliação para este evento
            criarPendenciaAvaliacao(evento);
        }
    }

    /**
     * Momento em que o evento efetivamente termina: horário de início + duração.
     * A soma pode atravessar a meia-noite (ex.: 23:00 + 2h termina no dia seguinte).
     * Sem horário de início cadastrado não há o que somar, então o evento é
     * considerado como indo até o fim do dia.
     */
    private LocalDateTime calcularTermino(Evento evento) {
        Time horarioInicio = evento.getHorarioInicio();
        if (horarioInicio == null)
            return LocalDateTime.of(evento.getData(), FIM_DO_DIA);

        LocalDateTime inicio = LocalDateTime.of(evento.getData(), horarioInicio.toLocalTime());
        return inicio.plus(duracaoOuPadrao(evento));
    }

    /**
     * Duração cadastrada do evento. Shows sem duração assumem o padrão configurado;
     * ensaios sem duração terminam no próprio horário de início.
     */
    private Duration duracaoOuPadrao(Evento evento) {
        Time duracao = evento.getDuracao();
        if (duracao != null)
            return Duration.ofSeconds(duracao.toLocalTime().toSecondOfDay());

        return evento instanceof Show
                ? Duration.ofHours(duracaoPadraoShowHoras)
                : Duration.ZERO;
    }

    /**
     * Cria uma pendência de avaliação para o evento realizado
     */
    private void criarPendenciaAvaliacao(Evento evento) {
        try {
            // Verifica se já existe uma pendência para este evento
            boolean jaExiste = pendenciaAvaliacaoEventoDao
                .findByIdEventoAndTipoEventoAndBandaIdAndAvaliadoFalse(
                    evento.getId(),
                    evento.getTipoEvento(),
                    evento.getBanda().getId()
                ).isPresent();
            
            if (!jaExiste) {
                PendenciaAvaliacaoEvento pendencia = new PendenciaAvaliacaoEvento();
                pendencia.setIdEvento(evento.getId());
                pendencia.setTipoEvento(evento.getTipoEvento());
                pendencia.setBanda(evento.getBanda());
                pendencia.setDataEvento(evento.getData());
                pendencia.setDataCriacao(LocalDateTime.now());
                pendencia.setQuantidadeAdiamentos(0);
                pendencia.setAvaliado(false);
                
                pendenciaAvaliacaoEventoDao.save(pendencia);
                
                System.out.println("Pendência de avaliação criada para: " + 
                    evento.getTipoEvento().getDescricao() + " da banda " + 
                    evento.getBanda().getNome() + " em " + evento.getData());
            }
        } catch (Exception e) {
            System.err.println("Erro ao criar pendência de avaliação: " + e.getMessage());
        }
    }
}
