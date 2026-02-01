package org.kaspi.fileuploader.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kaspi.fileuploader.configs.properties.S3MinioProperties;
import org.kaspi.fileuploader.domain.dto.UploadedFileDto;
import org.kaspi.fileuploader.services.FileStorageService;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioFileStorageService implements FileStorageService {

    private final S3Client s3MinioClient;
    private final S3MinioProperties properties;

    @Override
    public UploadedFileDto upload(File file) {
        try {
            // Генерируем уникальный ключ в storage
            String key = UUID.randomUUID() + "_" + file.getName();
            long contentLength = file.length();
            String contentType = Files.probeContentType(file.toPath());

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(key)
                    .contentType(contentType)
                    .contentLength(contentLength)
                    .build();

            s3MinioClient.putObject(putRequest, RequestBody.fromFile(file));

            return UploadedFileDto.builder()
                    .originalFileName(file.getName())
                    .fileSize(contentLength)
                    .fileType(contentType)
                    .bucket(properties.getBucket())
                    .storageKey(key)
                    .build();
        } catch (Exception e) {
            log.error("Failed to upload file to MinIO/S3", e);
            throw new RuntimeException("Upload failed", e);
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(storageKey)
                    .build();
            s3MinioClient.deleteObject(deleteRequest);
        } catch (Exception e) {
            log.error("Failed to delete file from MinIO/S3: " + storageKey, e);
            throw new RuntimeException("Delete failed", e);
        }
    }

    @Override
    public InputStream download(String storageKey) {
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(properties.getBucket())
                    .key(storageKey)
                    .build();

            return s3MinioClient.getObject(getRequest);

        } catch (Exception e) {
            log.error("Failed to download file from MinIO/S3: " + storageKey, e);
            throw new RuntimeException("Download failed", e);
        }
    }
}
