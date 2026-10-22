package com.baseapplication.core.utils;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Bloqueio temporário de login após falhas seguidas, por e-mail e por IP.
 *
 * <p>O nginx já limita requisições por IP, mas só para quem passa por ele; este limite
 * vale para qualquer caminho até o backend e, por contar falhas por e-mail, também segura
 * ataque distribuído contra uma conta específica.
 *
 * <p>Fica em memória porque o backend roda numa instância só (o Redis é opcional no
 * projeto). Reiniciar a aplicação zera os contadores, o que é aceitável aqui.
 */
@Slf4j
@Component
public class LimitadorTentativasLogin {

	public static final int MAX_FALHAS_POR_EMAIL = 10;
	public static final int MAX_FALHAS_POR_IP = 20;
	public static final int MAX_FALHAS_ADMIN_POR_IP = 5;
	public static final Duration JANELA = Duration.ofMinutes(15);
	public static final Duration BLOQUEIO = Duration.ofMinutes(15);

	private final Map<String, Contador> contadores = new ConcurrentHashMap<>();
	private Clock clock = Clock.systemUTC();

	private record Contador(int falhas, Instant inicioJanela, Instant bloqueadoAte) {
	}

	public static String chaveEmail(String email) {
		return "email:" + (email == null ? "" : email.trim().toLowerCase(Locale.ROOT));
	}

	public static String chaveIp(String ip) {
		return "ip:" + ip;
	}

	public static String chaveAdminIp(String ip) {
		return "admin-ip:" + ip;
	}

	/** Maior tempo de bloqueio restante entre as chaves; {@link Duration#ZERO} se nenhuma estiver bloqueada. */
	public Duration tempoRestanteBloqueio(String... chaves) {
		Instant agora = clock.instant();
		Duration maior = Duration.ZERO;
		for (String chave : chaves) {
			Contador c = contadores.get(chave);
			if (c != null && c.bloqueadoAte() != null && c.bloqueadoAte().isAfter(agora)) {
				Duration restante = Duration.between(agora, c.bloqueadoAte());
				if (restante.compareTo(maior) > 0)
					maior = restante;
			}
		}
		return maior;
	}

	public void registrarFalha(String chave, int maxFalhas) {
		Instant agora = clock.instant();
		Contador atualizado = contadores.compute(chave, (k, c) -> {
			if (c == null || c.inicioJanela().plus(JANELA).isBefore(agora)
					|| (c.bloqueadoAte() != null && !c.bloqueadoAte().isAfter(agora)))
				c = new Contador(0, agora, null);
			int falhas = c.falhas() + 1;
			Instant bloqueadoAte = falhas >= maxFalhas ? agora.plus(BLOQUEIO) : c.bloqueadoAte();
			return new Contador(falhas, c.inicioJanela(), bloqueadoAte);
		});
		if (atualizado.falhas() == maxFalhas)
			log.warn("Login bloqueado temporariamente para {} após {} falhas", chave, maxFalhas);
	}

	public void registrarSucesso(String chave) {
		contadores.remove(chave);
	}

	@Scheduled(fixedDelay = 10 * 60 * 1000)
	public void limparExpirados() {
		Instant agora = clock.instant();
		contadores.entrySet().removeIf(e -> {
			Contador c = e.getValue();
			boolean janelaVencida = c.inicioJanela().plus(JANELA).isBefore(agora);
			boolean semBloqueioAtivo = c.bloqueadoAte() == null || !c.bloqueadoAte().isAfter(agora);
			return janelaVencida && semBloqueioAtivo;
		});
	}
}
