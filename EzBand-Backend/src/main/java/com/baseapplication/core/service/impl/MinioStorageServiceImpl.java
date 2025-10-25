package com.baseapplication.core.service.impl;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
public class MinioStorageServiceImpl {

	@Value("${minio.user}")
	private String minioUser;

	@Value("${minio.password}")
	private String minioPassword;

//	public String uploadImage(MultipartFile file, String bucketName, String fileName) throws IOException {
//		try (S3Client s3 = S3Client.builder().region(Region.US_EAST_1).endpointOverride(URI.create("http://localhost:9000"))
//				.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials
//						.create(System.getenv("MINIO_ADMIN_USER"), System.getenv("MINIO_ADMIN_PASSWORD"))))
//				.serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build()).build()) {
//
//			PutObjectRequest putRequest = PutObjectRequest.builder().bucket(bucketName).key(fileName).build();
//
//			s3.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
//
//			System.out.println("Upload concluído: " + file.getOriginalFilename());
//
//			return String.format("http://localhost:9000/%s/%s", bucketName, fileName);
//		}
//	}

	public String uploadImage(MultipartFile file, String bucketName, String fileName) throws IOException {
		try (S3Client s3 = S3Client.builder()
				.region(Region.US_EAST_1)
				.endpointOverride(URI.create("ezband-storage"))
				.credentialsProvider(StaticCredentialsProvider.create(
						AwsBasicCredentials.create(minioUser, minioPassword)))
				.serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
				.build()) {

			PutObjectRequest putRequest = PutObjectRequest.builder()
					.bucket(bucketName)
					.key(fileName)
					.contentType(file.getContentType())
					.build();

			s3.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

			System.out.println("Upload concluído: " + file.getOriginalFilename());

//			return getPresignedUrl(bucketName, fileName);
			return String.format("ezband-storage/%s/%s", bucketName, fileName);
		}


	}

	public String getPresignedUrl(String bucketName, String fileName) {
		try (S3Presigner presigner = S3Presigner.builder()
				.region(Region.US_EAST_1)
				.endpointOverride(URI.create("ezband-storage"))
				.credentialsProvider(StaticCredentialsProvider.create(
						AwsBasicCredentials.create(minioUser, minioPassword)))
				.serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
				.build()) {

			GetObjectRequest getObjectRequest = GetObjectRequest.builder()
					.bucket(bucketName)
					.key(fileName)
					.build();

			GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
					.signatureDuration(Duration.ofMinutes(100)) // tempo de validade
					.getObjectRequest(getObjectRequest)
					.build();

			return presigner.presignGetObject(presignRequest).url().toString();
		}
	}
}
