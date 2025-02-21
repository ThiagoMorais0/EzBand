package com.baseapplication.core.service.impl;

import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.service.ImagemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImagemServiceImpl implements ImagemService {

    @Autowired
    private GoogleCloudStorageServiceImpl storageService;
    @Override
    public String salvarImagemNoBucket(MultipartFile imagem) {
        try {
            return storageService.uploadImage(imagem);
        } catch (IOException e) {
            throw new InternalException("Erro ao realizar upload da imagem");
        }
    }

    @Override
    public String saveImageAndGetUrl(MultipartFile image) {
        return image != null ? salvarImagemNoBucket(image) : "default";
    }
}
