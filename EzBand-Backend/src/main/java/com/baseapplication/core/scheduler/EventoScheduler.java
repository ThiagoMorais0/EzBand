package com.baseapplication.core.scheduler;

import com.baseapplication.core.dao.PendenciaAvaliacaoEventoDao;
import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.model.PendenciaAvaliacaoEvento;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.service.EventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventoScheduler {
    private final EventoService eventoService;
    private final PendenciaAvaliacaoEventoDao pendenciaAvaliacaoEventoDao;

    @Scheduled(fixedRate = 60000 * 10)
    public void atualizarEventosConcluidos() {
        LocalDate diaAtual = LocalDate.now();
        LocalTime horaAtual = LocalTime.now();
        LocalDate agora = LocalDate.now();
        System.out.println("Rodando job, data/hora atual: " + agora + " " + horaAtual);

        // busca eventos cuja data é menor que agora e status ainda não é CONCLUIDO
        List<Evento> eventos = eventoService.buscarComDataAnteriorAHoje();

        for (Evento evento : eventos) {

            if(evento.getHorarioInicio() == null)
                evento.setHorarioInicio(Time.valueOf(LocalTime.of(23, 59)));

            LocalTime inicio = evento.getHorarioInicio().toLocalTime();
            if (evento.getData().isBefore(diaAtual) || !inicio.isAfter(horaAtual)) {
                evento.setStatus(StatusEvento.REALIZADO);
                System.out.println("Evento concluído: " + evento.getBanda().getNome() + " em " + evento.getData() +
                        " às " + evento.getHorarioInicio().toString().substring(0, 5) + " no " + evento.getLocal());
                eventoService.salvar(evento);
                
                // Cria pendência de avaliação para este evento
                criarPendenciaAvaliacao(evento);
            }
        }
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
