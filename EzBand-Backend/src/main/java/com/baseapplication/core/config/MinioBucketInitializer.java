package com.baseapplication.core.config;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.PutBucketPolicyRequest;

@Component
public class MinioBucketInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(MinioBucketInitializer.class);

    @Value("${minio.user}")
    private String minioUser;

    @Value("${minio.password}")
    private String minioPassword;

    @Value("${minio.internal-url}")
    private String minioInternalUrl;

    @Value("${minio.bucket-name}")
    private String bucketNames;

    @Override
    public void run(ApplicationArguments args) {
        List<String> buckets = Arrays.stream(bucketNames.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        try (S3Client s3 = buildClient()) {
            for (String bucket : buckets) {
                ensureBucket(s3, bucket);
            }
        }
    }

    private void ensureBucket(S3Client s3, String bucket) {
        try {
            s3.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
            log.info("MinIO bucket '{}' already exists.", bucket);
        } catch (NoSuchBucketException e) {
            log.info("MinIO bucket '{}' not found — creating...", bucket);
            s3.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
            applyPublicReadPolicy(s3, bucket);
            log.info("MinIO bucket '{}' created and set to public-read.", bucket);
        }
    }

    private void applyPublicReadPolicy(S3Client s3, String bucket) {
        String policy = """
                {
                  "Version": "2012-10-17",
                  "Statement": [
                    {
                      "Effect": "Allow",
                      "Principal": {"AWS": ["*"]},
                      "Action": ["s3:GetObject"],
                      "Resource": ["arn:aws:s3:::%s/*"]
                    }
                  ]
                }
                """.formatted(bucket);

        s3.putBucketPolicy(PutBucketPolicyRequest.builder()
                .bucket(bucket)
                .policy(policy)
                .build());
    }

    private S3Client buildClient() {
        return S3Client.builder()
                .region(Region.US_EAST_1)
                .endpointOverride(URI.create(minioInternalUrl))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(minioUser, minioPassword)))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }
}
