package com.baseapplication.core.service.impl;

import java.io.IOException;
import java.net.URI;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class MinioStorageServiceImpl {
	public String uploadImage(MultipartFile file, String bucketName, String fileName) throws IOException {
		try (S3Client s3 = S3Client.builder().region(Region.US_EAST_1).endpointOverride(URI.create("http://minio:9000"))
				.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials
						.create(System.getenv("MINIO_ADMIN_USER"), System.getenv("MINIO_ADMIN_PASSWORD"))))
				.serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build()).build()) {

			PutObjectRequest putRequest = PutObjectRequest.builder().bucket(bucketName).key(fileName).build();

			s3.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

			System.out.println("Upload concluído: " + file.getOriginalFilename());

			return String.format("http://minio:9000/%s/%s", bucketName, fileName);
		}
	}
}
