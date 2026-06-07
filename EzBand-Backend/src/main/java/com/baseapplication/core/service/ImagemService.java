package com.baseapplication.core.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImagemService {
    String salvarImagemNoBucket(MultipartFile imagem, String bucketName, String fileName);
    String saveImageAndGetUrl(MultipartFile logo, String bucketName, String fileName);
    void deletarImagemPorUrl(String url);
}
