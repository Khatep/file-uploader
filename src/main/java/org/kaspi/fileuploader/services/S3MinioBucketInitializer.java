package org.kaspi.fileuploader.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kaspi.fileuploader.configs.properties.S3MinioProperties;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3MinioBucketInitializer implements ApplicationRunner {

    private final S3Client s3Client;
    private final S3MinioProperties properties;

    @Override
    public void run(ApplicationArguments args) {
        String bucket = properties.getBucket();

        if (!bucketExists(bucket)) {
            log.info("Bucket '{}' not found. Creating...", bucket);
            createBucket(bucket);
        } else {
            log.info("Bucket '{}' already exists", bucket);
        }
    }

    private boolean bucketExists(String bucket) {
        try {
            s3Client.headBucket(
                    HeadBucketRequest.builder()
                            .bucket(bucket)
                            .build()
            );
            return true;
        } catch (NoSuchBucketException e) {
            return false;
        } catch (S3Exception e) {
            if (e.statusCode() == 404) {
                return false;
            }
            throw e;
        }
    }

    private void createBucket(String bucket) {
        s3Client.createBucket(
                CreateBucketRequest.builder()
                        .bucket(bucket)
                        .build()
        );
    }
}
