package com.baseapplication.core.service.impl;

import com.baseapplication.core.dto.palco.TemplateMapaPalcoDTO;
import com.baseapplication.core.exception.InternalException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Carrega as sementes de mapa de resources/templates-palco. O mesmo JSON serve
 * para criar o mapa e para desenhar a previa no card de escolha, entao existe
 * um unico lugar definindo como cada formacao comeca.
 *
 * A ordem de CHAVES e a ordem em que os templates aparecem na tela.
 */
@Slf4j
@Service
public class TemplateMapaPalcoService {

    private static final String[] CHAVES = {
            "power-trio", "rock-5", "baile-7", "gospel", "sertanejo"
    };

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, TemplateMapaPalcoDTO> templates = new LinkedHashMap<>();

    @PostConstruct
    public void carregar() {
        for (String chave : CHAVES) {
            String caminho = "templates-palco/" + chave + ".json";
            try (InputStream in = new ClassPathResource(caminho).getInputStream()) {
                TemplateMapaPalcoDTO template = objectMapper.readValue(in, TemplateMapaPalcoDTO.class);
                template.setChave(chave);
                templates.put(chave, template);
            } catch (Exception e) {
                // Um template quebrado nao pode derrubar a aplicacao inteira: o
                // usuario perde aquela opcao, nao o modulo.
                log.error("Falha ao carregar template de mapa de palco: {}", caminho, e);
            }
        }
        log.info("Templates de mapa de palco carregados: {}", templates.keySet());
    }

    public List<TemplateMapaPalcoDTO> listar() {
        return new ArrayList<>(templates.values());
    }

    public TemplateMapaPalcoDTO buscar(String chave) {
        TemplateMapaPalcoDTO template = templates.get(chave);
        if (template == null) {
            throw new InternalException("Template de mapa de palco não encontrado: " + chave);
        }
        return template;
    }

    public boolean existe(String chave) {
        return chave != null && templates.containsKey(chave);
    }
}
