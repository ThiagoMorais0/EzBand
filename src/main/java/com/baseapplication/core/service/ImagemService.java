package com.baseapplication.core.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImagemService {
    String salvarImagemNoBucket(MultipartFile imagem);

    String saveImageAndGetUrl(MultipartFile logo);
}
