package com.baseapplication.core.dto;

import com.baseapplication.core.model.embedded.Endereco;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@NoArgsConstructor
public class EnderecoDTO {
    private String pais;
    private String estado;
    private String cidade;
    private String bairro;
    private String rua;
    private String numero;
    private String cep;

    public EnderecoDTO(Endereco endereco){
        if(endereco != null){
            BeanUtils.copyProperties(endereco, this);
        }
    }
}
