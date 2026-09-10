package com.baseapplication.core.dto;

import com.baseapplication.core.model.ImagemPublicacao;
import com.baseapplication.core.model.superClasses.Publicacao;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
public class PublicacaoDTO {
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * A data que vai para o front e uma String formatada, entao ordenar por ela colocaria
     * 31/01/2024 na frente de 01/12/2025. A ordenacao precisa acontecer na entidade, sobre o
     * LocalDateTime, antes do mapeamento; o id desempata publicacoes do mesmo minuto.
     */
    private static final Comparator<Publicacao> MAIS_RECENTES_PRIMEIRO =
            Comparator.comparing(Publicacao::getDataInclusao, Comparator.nullsFirst(Comparator.naturalOrder()))
                    .thenComparing(Publicacao::getId, Comparator.nullsFirst(Comparator.naturalOrder()))
                    .reversed();

    private String texto;
    private Long id;
    private List<String> imagens;
    private String dataPublicacao;

    public PublicacaoDTO(Publicacao publicacao){
        this.setImagens(publicacao.getImagens().stream().map(ImagemPublicacao::getUrl).toList());
        this.setTexto(publicacao.getTexto());
        this.setId(publicacao.getId());
        this.setDataPublicacao(publicacao.getDataInclusao() != null
                ? publicacao.getDataInclusao().format(FORMATO_DATA)
                : null);
    }

    /** Publicacoes de um perfil, da mais recente para a mais antiga. */
    public static List<PublicacaoDTO> maisRecentesPrimeiro(Collection<? extends Publicacao> publicacoes) {
        return publicacoes.stream()
                .sorted(MAIS_RECENTES_PRIMEIRO)
                .map(PublicacaoDTO::new)
                .toList();
    }
}
