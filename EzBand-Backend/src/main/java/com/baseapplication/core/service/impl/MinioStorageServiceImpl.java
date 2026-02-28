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

	// Nova variável para a rede interna do Docker (Upload)
	@Value("${minio.internal-url}")
	private String minioInternalUrl;

	// Nova variável para a rede pública (Aplicativo)
	@Value("${minio.external-url}")
	private String minioExternalUrl;

	public String uploadImage(MultipartFile file, String bucketName, String fileName) throws IOException {
		// Usa a URL INTERNA para conectar no S3
		try (S3Client s3 = S3Client.builder()
				.region(Region.US_EAST_1)
				.endpointOverride(URI.create(minioInternalUrl))
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

			// Retorna a URL EXTERNA para ser salva no banco e acessada pelo App
			return String.format("%s/%s/%s", minioExternalUrl, bucketName, fileName);
		}
	}

	public String getPresignedUrl(String bucketName, String fileName) {
		// Usa a URL INTERNA para conectar no S3 e gerar a assinatura
		try (S3Presigner presigner = S3Presigner.builder()
				.region(Region.US_EAST_1)
				.endpointOverride(URI.create(minioInternalUrl))
				.credentialsProvider(StaticCredentialsProvider.create(
						AwsBasicCredentials.create(minioUser, minioPassword)))
				.serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
				.build()) {

			GetObjectRequest getObjectRequest = GetObjectRequest.builder()
					.bucket(bucketName)
					.key(fileName)
					.build();

			GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
					.signatureDuration(Duration.ofMinutes(100))
					.getObjectRequest(getObjectRequest)
					.build();

			// A AWS SDK vai usar o endpointOverride para montar a URL,
			// Se presigned URLs forem enviadas ao App, elas precisariam da URL externa.
			// Como você montou os buckets como públicos e está retornando a string formatada no upload,
			// a presigned não será um problema crítico agora.
			String url = presigner.presignGetObject(presignRequest).url().toString();

			// Troca a base interna pela externa no retorno do Presigned (Garantia extra)
			return url.replace(minioInternalUrl, minioExternalUrl);
		}
	}
}