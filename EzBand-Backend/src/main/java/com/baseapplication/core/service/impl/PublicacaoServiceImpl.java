package com.baseapplication.core.service.impl;

import com.baseapplication.core.dao.ImagemPublicacaoDao;
import com.baseapplication.core.dao.PublicacaoDao;
import com.baseapplication.core.dto.NovaPublicacaoDTO;
import com.baseapplication.core.model.Banda;
import com.baseapplication.core.model.ImagemPublicacao;
import com.baseapplication.core.model.Usuario;
import com.baseapplication.core.model.publicacao.PublicacaoBanda;
import com.baseapplication.core.model.publicacao.PublicacaoUsuario;
import com.baseapplication.core.model.superClasses.Publicacao;
import com.baseapplication.core.service.ImagemService;
import com.baseapplication.core.service.PublicacaoService;
import com.baseapplication.core.utils.FileUtils;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicacaoServiceImpl implements PublicacaoService {

    private final PublicacaoDao publicacaoDao;
    private final ImagemPublicacaoDao imagemPublicacaoDao;
    private final ImagemService imagemService;
    private final EntityManager entityManager;

    @Override
    @Transactional
    public void publicar(NovaPublicacaoDTO publicacao, List<MultipartFile> imagens) {
        switch (publicacao.getTipoPublicante()){
            case USUARIO -> criarPublicacaoUsuario(publicacao, imagens);
            case BANDA -> criarPublicacaoBanda(publicacao, imagens);
        }
    }

    private void criarPublicacaoBanda(NovaPublicacaoDTO publicacao, List<MultipartFile> imagens) {
        Banda banda = entityManager.find(Banda.class, publicacao.getIdPublicante());

        PublicacaoBanda publicacaoBanda = new PublicacaoBanda();
        publicacaoBanda.setDataInclusao(LocalDateTime.now());
        publicacaoBanda.setTexto(publicacao.getTexto());
        publicacaoBanda.setBanda(banda);
        publicacaoBanda = publicacaoDao.save(publicacaoBanda);

        List<ImagemPublicacao> imagensPublicacao = new ArrayList<>();
        int count = 0;
        for(MultipartFile imagem : imagens){
            String urlImagem = imagemService.saveImageAndGetUrl(imagem, "userposts",
                    new Date().toString().replaceAll("\\s+", "") + "banda_" + banda.getId()  + "_" + count++ +  "." + FileUtils.getSufix(imagem));
            imagensPublicacao.add(new ImagemPublicacao(null, urlImagem, publicacaoBanda));
        }
        publicacaoBanda.setImagens(imagensPublicacao);
        publicacaoDao.save(publicacaoBanda);
    }

    @Override
    public void editarTextoPublicacao(Long idPublicacao, String texto) {
        Publicacao publicacao = publicacaoDao.findById(idPublicacao).orElseThrow(() -> new RuntimeException("Publicacao nao encontrada"));
        publicacao.setTexto(texto);
        publicacaoDao.save(publicacao);
    }

    @Override
    public void excluirPublicacao(Long idPublicacao) {
        imagemPublicacaoDao.deletarPorIdPublicacao(idPublicacao);
        publicacaoDao.deletarPeloId(idPublicacao);
    }


    private void criarPublicacaoUsuario(NovaPublicacaoDTO publicacao, List<MultipartFile> imagens) {
        Usuario usuario = entityManager.find(Usuario.class, publicacao.getIdPublicante());

        PublicacaoUsuario publicacaoUsuario = new PublicacaoUsuario();
        publicacaoUsuario.setDataInclusao(LocalDateTime.now());
        publicacaoUsuario.setTexto(publicacao.getTexto());
        publicacaoUsuario.setUsuario(usuario);
        publicacaoUsuario = publicacaoDao.save(publicacaoUsuario);

        List<ImagemPublicacao> imagensPublicacao = new ArrayList<>();
        int count = 0;
        for(MultipartFile imagem : imagens){
            String urlImagem = imagemService.saveImageAndGetUrl(imagem, "userposts",
                    new Date().toString().replaceAll("\\s+", "") + "usuario_" + usuario.getId() + "_" + count++ + "." + FileUtils.getSufix(imagem));
            imagensPublicacao.add(new ImagemPublicacao(null, urlImagem, publicacaoUsuario));
        }
        publicacaoUsuario.setImagens(imagensPublicacao);
        publicacaoDao.save(publicacaoUsuario);
    }
}
