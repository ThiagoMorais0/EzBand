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
	private MinioStorageServiceImpl minioStorageServiceImpl;

	@Override
	public String salvarImagemNoBucket(MultipartFile imagem, String bucketName, String fileName) {
		System.out.println("imagem: " + imagem);
		try {
			return minioStorageServiceImpl.uploadImage(imagem, bucketName, fileName);
		} catch (IOException e) {
			throw new InternalException("Erro ao realizar upload da imagem");
		}
	}

	@Override
	public String saveImageAndGetUrl(MultipartFile image, String bucketName, String fileName) {
		System.out.println("imagem: " + image);
		return image != null ? salvarImagemNoBucket(image, bucketName, fileName) : "default";
	}

	@Override
	public void deletarImagemPorUrl(String url) {
		minioStorageServiceImpl.deleteImageByUrl(url);
	}
}
