package com.baseapplication.core.dto;

import com.baseapplication.core.model.ImagemPublicacao;
import com.baseapplication.core.model.superClasses.Publicacao;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
public class PublicacaoDTO {
    private String texto;
    private Long id;
    private List<String> imagens;
    private String dataPublicacao;

    public PublicacaoDTO(Publicacao publicacao){
        this.setImagens(publicacao.getImagens().stream().map(ImagemPublicacao::getUrl).toList());
        this.setTexto(publicacao.getTexto());
        this.setId(publicacao.getId());
        this.setDataPublicacao(publicacao.getDataInclusao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    }
}
