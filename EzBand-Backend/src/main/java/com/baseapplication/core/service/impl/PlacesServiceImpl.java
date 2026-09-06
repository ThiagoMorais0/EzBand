package com.baseapplication.core.service.impl;

import com.baseapplication.core.dto.EnderecoDTO;
import com.baseapplication.core.dto.UserPrincipal;
import com.baseapplication.core.dto.places.BuscaPlacesDTO;
import com.baseapplication.core.dto.places.PlaceDetalheDTO;
import com.baseapplication.core.dto.places.PlaceSugestaoDTO;
import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.exception.InvalidParamException;
import com.baseapplication.core.exception.RestrictionException;
import com.baseapplication.core.service.PlacesService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Proxy para a Places API (New) do Google.
 *
 * A chave nunca sai do servidor: o front chama /places/* autenticado e este service
 * repassa ao Google. Ver PlacesController.
 *
 * Custo: o nome do local vem da sugestao do autocomplete (gratuito) e o Place Details
 * pede apenas campos do SKU Essentials. Pedir displayName aqui jogaria a chamada para
 * o SKU Pro (US$ 17/mil em vez de US$ 5/mil) sem ganho nenhum.
 */
@Slf4j
@Service
public class PlacesServiceImpl implements PlacesService {

    private static final String BASE_URL = "https://places.googleapis.com";

    /** Campos do SKU Essentials. Nao acrescentar displayName/rating/telefone aqui. */
    private static final String FIELD_MASK_DETALHES = "id,formattedAddress,location,addressComponents";

    private static final int MAX_SUGESTOES = 8;
    private static final int MIN_CARACTERES_BUSCA = 3;

    /** Raio do vies de localizacao: cobre uma regiao metropolitana. */
    private static final double RAIO_BIAS_METROS = 50_000;

    /** Teto por usuario. Segunda linha de defesa; a quota do console e a primeira. */
    private static final int LIMITE_BUSCAS_POR_JANELA = 60;
    private static final Duration JANELA_LIMITE = Duration.ofMinutes(10);

    /** Acima disto, entradas expiradas do mapa de contadores sao descartadas. */
    private static final int MAX_USUARIOS_RASTREADOS = 5_000;

    private final String apiKey;
    private final RestClient restClient;
    private final Map<Long, ContadorJanela> contadores = new ConcurrentHashMap<>();

    public PlacesServiceImpl(@Value("${google.maps.api-key:}") String apiKey) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5_000);
        factory.setReadTimeout(8_000);

        this.restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .requestFactory(factory)
                .build();

        if (!disponivel()) {
            log.warn("google.maps.api-key nao configurada - a busca de locais ficara desativada");
        }
    }

    @Override
    public boolean disponivel() {
        return StringUtils.hasText(apiKey);
    }

    @Override
    public List<PlaceSugestaoDTO> buscarSugestoes(BuscaPlacesDTO dto) {
        exigirDisponivel();

        String texto = dto.getTexto() == null ? "" : dto.getTexto().trim();
        if (texto.length() < MIN_CARACTERES_BUSCA) {
            throw new InvalidParamException("Digite pelo menos " + MIN_CARACTERES_BUSCA + " caracteres para buscar");
        }

        registrarUso();

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("input", texto);
        body.put("languageCode", "pt-BR");
        body.put("regionCode", "BR");
        body.put("includedRegionCodes", List.of("br"));

        if (StringUtils.hasText(dto.getSessionToken())) {
            body.put("sessionToken", dto.getSessionToken());
        }

        // Enviesa para perto da cidade informada sem excluir resultados de fora dela.
        if (dto.getLatitude() != null && dto.getLongitude() != null) {
            body.put("locationBias", Map.of("circle", Map.of(
                    "center", Map.of("latitude", dto.getLatitude(), "longitude", dto.getLongitude()),
                    "radius", RAIO_BIAS_METROS
            )));
        }

        JsonNode resposta = chamar(() -> restClient.post()
                .uri("/v1/places:autocomplete")
                .header("X-Goog-Api-Key", apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(JsonNode.class));

        List<PlaceSugestaoDTO> sugestoes = new ArrayList<>();
        JsonNode lista = resposta.path("suggestions");

        for (JsonNode item : lista) {
            JsonNode predicao = item.path("placePrediction");
            if (predicao.isMissingNode()) {
                continue; // queryPrediction: sugestao de busca generica, nao e um local
            }

            String placeId = predicao.path("placeId").asText(null);
            if (placeId == null) {
                continue;
            }

            JsonNode formato = predicao.path("structuredFormat");
            String nome = formato.path("mainText").path("text").asText(null);
            String endereco = formato.path("secondaryText").path("text").asText("");

            // Sem structuredFormat, o texto corrido ainda identifica o local.
            if (!StringUtils.hasText(nome)) {
                nome = predicao.path("text").path("text").asText(null);
            }
            if (!StringUtils.hasText(nome)) {
                continue;
            }

            sugestoes.add(new PlaceSugestaoDTO(placeId, nome, endereco));

            if (sugestoes.size() >= MAX_SUGESTOES) {
                break;
            }
        }

        return sugestoes;
    }

    @Override
    public PlaceDetalheDTO buscarDetalhes(String placeId, String sessionToken) {
        exigirDisponivel();

        if (!StringUtils.hasText(placeId)) {
            throw new InvalidParamException("placeId e obrigatorio");
        }

        registrarUso();

        JsonNode resposta = chamar(() -> restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/v1/places/{placeId}")
                            .queryParam("languageCode", "pt-BR")
                            .queryParam("regionCode", "BR");
                    if (StringUtils.hasText(sessionToken)) {
                        uriBuilder.queryParam("sessionToken", sessionToken);
                    }
                    return uriBuilder.build(placeId);
                })
                .header("X-Goog-Api-Key", apiKey)
                .header("X-Goog-FieldMask", FIELD_MASK_DETALHES)
                .retrieve()
                .body(JsonNode.class));

        PlaceDetalheDTO detalhe = new PlaceDetalheDTO();
        detalhe.setPlaceId(resposta.path("id").asText(placeId));
        detalhe.setEnderecoFormatado(resposta.path("formattedAddress").asText(""));
        detalhe.setEndereco(montarEndereco(resposta, placeId));
        return detalhe;
    }

    /**
     * Traduz addressComponents do Google para o Endereco do EzBand.
     *
     * Cuidados validados contra enderecos brasileiros reais:
     * - "rua" usa longText: o shortText abrevia "Avenida" para "Av.".
     * - "cidade" cai para administrative_area_level_2 quando nao ha locality. Enderecos
     *   de capital costumam vir sem locality e a cidade ficaria vazia.
     * - "estado" usa shortText para gravar a UF ("SP") e nao "Sao Paulo".
     * - numero e cep faltam com frequencia em POIs; ficam vazios e o usuario completa.
     */
    private EnderecoDTO montarEndereco(JsonNode resposta, String placeId) {
        Map<String, JsonNode> porTipo = new HashMap<>();
        for (JsonNode componente : resposta.path("addressComponents")) {
            for (JsonNode tipo : componente.path("types")) {
                porTipo.putIfAbsent(tipo.asText(), componente);
            }
        }

        EnderecoDTO endereco = new EnderecoDTO();
        endereco.setNumero(textoLongo(porTipo, "street_number"));
        endereco.setRua(textoLongo(porTipo, "route"));
        endereco.setBairro(primeiroTextoLongo(porTipo, "sublocality_level_1", "sublocality", "neighborhood"));
        endereco.setCidade(primeiroTextoLongo(porTipo, "locality", "administrative_area_level_2"));
        endereco.setEstado(textoCurto(porTipo, "administrative_area_level_1"));
        endereco.setCep(textoLongo(porTipo, "postal_code"));
        endereco.setComplemento("");

        String pais = textoLongo(porTipo, "country");
        endereco.setPais(StringUtils.hasText(pais) ? pais : "Brasil");

        endereco.setGooglePlaceId(placeId);

        JsonNode local = resposta.path("location");
        if (local.hasNonNull("latitude") && local.hasNonNull("longitude")) {
            endereco.setLatitude(local.get("latitude").asDouble());
            endereco.setLongitude(local.get("longitude").asDouble());
        }

        return endereco;
    }

    private String textoLongo(Map<String, JsonNode> porTipo, String tipo) {
        JsonNode componente = porTipo.get(tipo);
        return componente == null ? "" : componente.path("longText").asText("");
    }

    private String textoCurto(Map<String, JsonNode> porTipo, String tipo) {
        JsonNode componente = porTipo.get(tipo);
        return componente == null ? "" : componente.path("shortText").asText("");
    }

    private String primeiroTextoLongo(Map<String, JsonNode> porTipo, String... tipos) {
        for (String tipo : tipos) {
            String valor = textoLongo(porTipo, tipo);
            if (StringUtils.hasText(valor)) {
                return valor;
            }
        }
        return "";
    }

    private JsonNode chamar(ChamadaGoogle chamada) {
        try {
            JsonNode resposta = chamada.executar();
            if (resposta == null) {
                throw new InternalException("Resposta vazia do Google Places");
            }
            return resposta;
        } catch (RestClientException e) {
            log.error("Falha ao consultar o Google Places: {}", e.getMessage(), e);
            throw new InternalException("Nao foi possivel consultar o Google Places");
        }
    }

    private void exigirDisponivel() {
        if (!disponivel()) {
            throw new InternalException("Busca de locais indisponivel: google.maps.api-key nao configurada");
        }
    }

    /**
     * Conta chamadas por usuario numa janela deslizante simples.
     * Em memoria de proposito: e um teto anti-abuso, nao contabilidade.
     */
    private void registrarUso() {
        Long idUsuario = idUsuarioLogado();
        if (idUsuario == null) {
            return;
        }

        // Sem isto o mapa guardaria uma entrada por usuario para sempre.
        if (contadores.size() > MAX_USUARIOS_RASTREADOS) {
            long limite = System.currentTimeMillis() - JANELA_LIMITE.toMillis();
            contadores.values().removeIf(c -> c.inicio < limite);
        }

        ContadorJanela contador = contadores.compute(idUsuario, (chave, atual) -> {
            long agora = System.currentTimeMillis();
            if (atual == null || agora - atual.inicio > JANELA_LIMITE.toMillis()) {
                return new ContadorJanela(agora);
            }
            return atual;
        });

        if (contador.chamadas.incrementAndGet() > LIMITE_BUSCAS_POR_JANELA) {
            log.warn("Usuario {} excedeu o limite de buscas de local", idUsuario);
            throw new RestrictionException("Muitas buscas em pouco tempo. Aguarde alguns minutos.");
        }
    }

    private Long idUsuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            return principal.getId();
        }
        return null;
    }

    private static final class ContadorJanela {
        private final long inicio;
        private final AtomicInteger chamadas = new AtomicInteger();

        private ContadorJanela(long inicio) {
            this.inicio = inicio;
        }
    }

    @FunctionalInterface
    private interface ChamadaGoogle {
        JsonNode executar();
    }
}
