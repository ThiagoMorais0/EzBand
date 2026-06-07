package com.baseapplication.core.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Service
public class MinioStorageServiceImpl {

	private static final long MAX_SIZE_BYTES = 500 * 1024; // 500 KB — não comprime abaixo deste limite
	private static final double COMPRESSION_QUALITY = 0.75;

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
		byte[] imageBytes = comprimirSeNecessario(file);

		try (S3Client s3 = buildS3Client()) {
			PutObjectRequest putRequest = PutObjectRequest.builder()
					.bucket(bucketName)
					.key(fileName)
					.contentType(resolveContentType(file, fileName))
					.build();

			s3.putObject(putRequest, RequestBody.fromBytes(imageBytes));

			System.out.println("Upload concluído: " + file.getOriginalFilename()
					+ " | tamanho original: " + file.getSize() + "B | tamanho final: " + imageBytes.length + "B");

			return String.format("%s/%s/%s", minioExternalUrl, bucketName, fileName);
		}
	}

	public void deleteImage(String bucketName, String fileName) {
		try (S3Client s3 = buildS3Client()) {
			s3.deleteObject(DeleteObjectRequest.builder()
					.bucket(bucketName)
					.key(fileName)
					.build());
			System.out.println("Imagem deletada do Minio: " + bucketName + "/" + fileName);
		} catch (NoSuchKeyException e) {
			System.out.println("Imagem não encontrada no Minio (ignorando): " + bucketName + "/" + fileName);
		}
	}

	/**
	 * Extrai bucket e fileName de uma URL completa do Minio e deleta o objeto.
	 * Ignora silenciosamente URLs nulas, "default" ou que não pertençam a este servidor.
	 */
	public void deleteImageByUrl(String url) {
		if (url == null || url.isBlank() || url.equals("default")) return;

		String base = minioExternalUrl.endsWith("/") ? minioExternalUrl : minioExternalUrl + "/";
		if (!url.startsWith(base)) return;

		String path = url.substring(base.length()); // "bucketName/fileName"
		int slash = path.indexOf('/');
		if (slash < 0) return;

		String bucketName = path.substring(0, slash);
		String fileName   = path.substring(slash + 1);
		deleteImage(bucketName, fileName);
	}

	public String getPresignedUrl(String bucketName, String fileName) {
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

			String url = presigner.presignGetObject(presignRequest).url().toString();
			return url.replace(minioInternalUrl, minioExternalUrl);
		}
	}

	// --- helpers ---

	private S3Client buildS3Client() {
		return S3Client.builder()
				.region(Region.US_EAST_1)
				.endpointOverride(URI.create(minioInternalUrl))
				.credentialsProvider(StaticCredentialsProvider.create(
						AwsBasicCredentials.create(minioUser, minioPassword)))
				.serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
				.build();
	}

	private byte[] comprimirSeNecessario(MultipartFile file) throws IOException {
		String contentType = file.getContentType();
		boolean isImage = contentType != null && contentType.startsWith("image/");
		boolean isGif   = contentType != null && contentType.equals("image/gif");

		if (!isImage || isGif || file.getSize() <= MAX_SIZE_BYTES) {
			return file.getBytes();
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Thumbnails.of(file.getInputStream())
				.scale(1.0)
				.outputQuality(COMPRESSION_QUALITY)
				.outputFormat(resolveOutputFormat(file))
				.toOutputStream(out);
		return out.toByteArray();
	}

	private String resolveOutputFormat(MultipartFile file) {
		String ct = file.getContentType();
		if (ct == null) return "jpeg";
		return switch (ct) {
			case "image/png"  -> "png";
			case "image/webp" -> "jpeg"; // thumbnailator não suporta webp output
			default           -> "jpeg";
		};
	}

	private String resolveContentType(MultipartFile file, String fileName) {
		String ct = file.getContentType();
		if (ct != null && !ct.isBlank()) return ct;
		String lower = fileName.toLowerCase();
		if (lower.endsWith(".png"))  return "image/png";
		if (lower.endsWith(".gif"))  return "image/gif";
		if (lower.endsWith(".webp")) return "image/webp";
		return "image/jpeg";
	}
}