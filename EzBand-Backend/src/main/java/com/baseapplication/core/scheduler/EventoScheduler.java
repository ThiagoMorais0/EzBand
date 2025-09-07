package com.baseapplication.core.scheduler;

import com.baseapplication.core.enums.StatusEvento;
import com.baseapplication.core.model.superClasses.Evento;
import com.baseapplication.core.service.EventoService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EventoScheduler {
    private final EventoService eventoService;

    @Scheduled(fixedRate = 60000)
    public void atualizarEventosConcluidos() {
        LocalDate diaAtual = LocalDate.now();
        LocalTime horaAtual = LocalTime.now();
        LocalDate agora = LocalDate.now();
        System.out.println("Rodando job, data/hora atual: " + agora);

        // busca eventos cuja data é menor que agora e status ainda não é CONCLUIDO
        List<Evento> eventos = eventoService.buscarComDataAnteriorAHoje();

        for (Evento evento : eventos) {
            LocalTime inicio = evento.getHorarioInicio().toLocalTime();
            if (evento.getData().isBefore(diaAtual) || !inicio.isAfter(horaAtual)) {
                evento.setStatus(StatusEvento.REALIZADO);
                eventoService.salvar(evento);
            }
        }

        if (!eventos.isEmpty()) {
            System.out.println(eventos.size() + " eventos atualizados para CONCLUIDO.");
        }
    }
}
