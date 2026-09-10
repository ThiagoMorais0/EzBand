package com.baseapplication.core.dto;

import com.baseapplication.core.enums.TipoEvento;
import com.baseapplication.core.enums.TipoItemRepertorio;
import com.baseapplication.core.enums.Tonalidade;
import com.baseapplication.core.model.RepertorioEvento;
import com.baseapplication.core.model.RepertorioEventoId;
import com.baseapplication.core.model.embedded.Musica;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.sql.Time;

/**
 * Um item do repertório do evento — música ou momento.
 *
 * <p>Um MOMENTO ("dar boa noite", "pausa para água") usa {@code titulo} como texto,
 * {@code duracao} como estimativa e {@code observacao} como o roteiro em texto livre —
 * os tópicos que quem fala vai puxar no palco. Tudo o mais é de música e vem nulo, o que é
 * justamente por que os dois caminhos abaixo tratam tonalidade com cuidado: ela não tem
 * valor neutro — {@code 0} já significa "tom original" — e
 * {@link Tonalidade#encontrarPeloNumero} lança exceção para nulo.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RepertorioEventoDTO {
    private String titulo;
    private Integer indice;
    private String artista;
    private String descricao;
    private String observacao;
    private String duracao;
    private Integer tonalidade;
    private String bloco;
    private String urlYoutube;
    private String urlSpotify;
    private String letra;
    private Integer bpm;
    private TipoItemRepertorio tipoItem;

    public boolean ehMomento() {
        return tipoItem == TipoItemRepertorio.MOMENTO;
    }

    public static RepertorioEvento toEntity(RepertorioEventoDTO repertorioEventoDTO,
                                      Long idEvento,
                                      Long idBanda,
                                      TipoEvento tipoEvento){
        RepertorioEvento repertorioEvento = new RepertorioEvento();

        repertorioEvento.setId(new RepertorioEventoId(
                repertorioEventoDTO.indice,
                idEvento,
                idBanda,
                tipoEvento));

        Time duracao = repertorioEventoDTO.duracao != null && !repertorioEventoDTO.duracao.isBlank()
                ? Time.valueOf(repertorioEventoDTO.duracao)
                : null;

        if (repertorioEventoDTO.ehMomento()) {
            repertorioEvento.setTipoItem(TipoItemRepertorio.MOMENTO);
            // Só o que um momento tem: texto, anotações e estimativa. Gravar os campos de
            // música com valor "vazio" faria a linha parecer uma música mal preenchida na
            // primeira consulta que esquecesse o filtro — melhor que ela seja obviamente
            // outra coisa. `observacao` é a mesma coluna TEXT que guarda a nota da música:
            // num momento ela é o roteiro, e nenhuma consulta mistura os dois porque
            // `tipo_item` já separa.
            repertorioEvento.setMusica(new Musica(
                    repertorioEventoDTO.titulo, null, null,
                    repertorioEventoDTO.observacao,
                    duracao, null, null, null, null, null));
            return repertorioEvento;
        }

        repertorioEvento.setTipoItem(TipoItemRepertorio.MUSICA);
        repertorioEvento.setMusica(new Musica(
                repertorioEventoDTO.titulo,
                repertorioEventoDTO.artista,
                repertorioEventoDTO.descricao,
                repertorioEventoDTO.observacao,
                duracao,
                repertorioEventoDTO.tonalidade != null
                        ? Tonalidade.encontrarPeloNumero(repertorioEventoDTO.getTonalidade())
                        : Tonalidade.ORIGINAL,
                repertorioEventoDTO.urlYoutube,
                repertorioEventoDTO.urlSpotify,
                repertorioEventoDTO.letra,
                repertorioEventoDTO.bpm)
        );
        // `bloco` fica de fora de propósito: o front manda 'default' fixo em todo save e nunca
        // persistiu, então gravá-lo agora faria o Modo Palco desenhar um separador escrito
        // "default" no topo de todo setlist. Agrupamento por bloco é outra feature.
        return repertorioEvento;
    }

    public RepertorioEventoDTO(RepertorioEvento entity){
        BeanUtils.copyProperties(entity.getMusica(), this);
        if(entity.getMusica().getDuracao() != null){
            this.duracao = entity.getMusica().getDuracao().toString();
        }
        // Momento não tem tom, e música gravada antes do campo existir também pode não ter.
        this.tonalidade = entity.getMusica().getTonalidade() != null
                ? entity.getMusica().getTonalidade().getNumero()
                : null;
        this.bloco = entity.getBloco();
        this.indice = entity.getId().getIndice();
        this.tipoItem = entity.getTipoItem();
    }
}
