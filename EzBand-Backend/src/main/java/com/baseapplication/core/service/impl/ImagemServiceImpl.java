package com.baseapplication.core.service.impl;

import com.baseapplication.core.exception.InternalException;
import com.baseapplication.core.service.ImagemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
public class ImagemServiceImpl implements ImagemService {

	@Autowired
	private MinioStorageServiceImpl minioStorageServiceImpl;

	@Override
	public String salvarImagemNoBucket(MultipartFile imagem, String bucketName, String fileName) {
		try {
			return minioStorageServiceImpl.uploadImage(imagem, bucketName, fileName);
		} catch (IOException | RuntimeException e) {
			log.error("Erro ao fazer upload da imagem para bucket '{}': {}", bucketName, e.getMessage(), e);
			throw new InternalException("Erro ao realizar upload da imagem: " + e.getMessage());
		}
	}

	@Override
	public String saveImageAndGetUrl(MultipartFile image, String bucketName, String fileName) {
		return image != null ? salvarImagemNoBucket(image, bucketName, fileName) : "default";
	}

	@Override
	public void deletarImagemPorUrl(String url) {
		minioStorageServiceImpl.deleteImageByUrl(url);
	}
}
