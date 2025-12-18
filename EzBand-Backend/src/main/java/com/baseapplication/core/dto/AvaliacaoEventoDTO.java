package com.baseapplication.core.dto;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.model.AvaliacaoEvento;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoEventoDTO {
    
    private Long id;
    
    @NotNull(message = "ID do evento é obrigatório")
    private Long idEvento;
    
    @NotNull(message = "Tipo do evento é obrigatório")
    private TipoEvento tipoEvento;
    
    @NotNull(message = "ID da banda é obrigatório")
    private Long idBanda;
    
    // Avaliação do local/estúdio (opcional, 1 a 5)
    @Min(value = 1, message = "Qualidade do som deve ser entre 1 e 5")
    @Max(value = 5, message = "Qualidade do som deve ser entre 1 e 5")
    private Integer qualidadeSom;
    
    @Min(value = 1, message = "Estrutura deve ser entre 1 e 5")
    @Max(value = 5, message = "Estrutura deve ser entre 1 e 5")
    private Integer estrutura;
    
    @Min(value = 1, message = "Organização deve ser entre 1 e 5")
    @Max(value = 5, message = "Organização deve ser entre 1 e 5")
    private Integer organizacao;
    
    @Min(value = 1, message = "Atendimento deve ser entre 1 e 5")
    @Max(value = 5, message = "Atendimento deve ser entre 1 e 5")
    private Integer atendimento;
    
    // Avaliação geral do evento (obrigatório, 1 a 5)
    @NotNull(message = "Experiência geral é obrigatória")
    @Min(value = 1, message = "Experiência geral deve ser entre 1 e 5")
    @Max(value = 5, message = "Experiência geral deve ser entre 1 e 5")
    private Integer experienciaGeral;
    
    // Comentários opcionais
    private String comentarioPositivo;
    private String comentarioNegativo;
    private String observacoes;
    
    // Campos adicionais para resposta
    private String nomeUsuarioAvaliador;
    private String dataAvaliacao;
    
    // Informações do local avaliado (para histórico)
    private String nomeLocal;
    private Long idLocal;
    private String tipoLocal; // "LOCAL_EVENTO", "ESTUDIO" ou null
    
    public AvaliacaoEventoDTO(AvaliacaoEvento avaliacao) {
        this.id = avaliacao.getId();
        this.idEvento = avaliacao.getIdEvento();
        this.tipoEvento = avaliacao.getTipoEvento();
        this.idBanda = avaliacao.getBanda().getId();
        this.qualidadeSom = avaliacao.getQualidadeSom();
        this.estrutura = avaliacao.getEstrutura();
        this.organizacao = avaliacao.getOrganizacao();
        this.atendimento = avaliacao.getAtendimento();
        this.experienciaGeral = avaliacao.getExperienciaGeral();
        this.comentarioPositivo = avaliacao.getComentarioPositivo();
        this.comentarioNegativo = avaliacao.getComentarioNegativo();
        this.observacoes = avaliacao.getObservacoes();
        this.nomeUsuarioAvaliador = avaliacao.getUsuarioAvaliador().getNome();
        this.dataAvaliacao = avaliacao.getDataAvaliacao().toString();
    }
}
