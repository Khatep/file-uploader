package org.kaspi.fileuploader.services;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.kaspi.fileuploader.domain.dto.FileRequestDto;
import org.kaspi.fileuploader.domain.dto.UploadedFileDto;
import org.kaspi.fileuploader.domain.exceptions.DuplicateFileException;
import org.kaspi.fileuploader.domain.models.FileMetadata;
import org.kaspi.fileuploader.domain.repositories.FilesMetadataRepository;
import org.kaspi.fileuploader.utils.HashUtils;
import org.kaspi.fileuploader.utils.TempFileUtils;
import org.kaspi.fileuploader.utils.mappers.FileMetadataMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FilesMetadataRepository filesMetadataRepository;
    private final FileStorageService fileStorageService;

    private final Executor taskExecutor;
    private final ObjectProvider<FileService> selfProvider;

    public void createAndSave(FileRequestDto dto) {
        try {
            // Сохраняем MultipartFile во временный файл
            File tempFile = TempFileUtils.saveTempFile(dto);
            String fileHash = HashUtils.sha256(tempFile);

            CompletableFuture
                    .supplyAsync(() -> fileStorageService.upload(tempFile), taskExecutor)
                    .thenAccept((uploadedFileDto) ->
                             saveWithCompensation(uploadedFileDto, fileHash, dto.getUserId())
                    )
                    .exceptionally(ex -> {
                        log.error("Failed to process proof of address", ex);
                        //TODO: KAFKA for PUSH
                        return null;
                    })
                    .whenComplete((res, ex) -> {
                        TempFileUtils.deleteFile(tempFile);
                    });
        } catch (Exception e) {
            log.error("Failed to process proof of address", e);
        }
    }

    private void saveWithCompensation(UploadedFileDto document, String fileHash, Long userId) {
        Try.run(() -> selfProvider.getObject().transactionalSave(document, fileHash, userId))
                .onFailure(ex -> {
                    fileStorageService.delete(document.getStorageKey());

                    if (ex instanceof DuplicateFileException) {
                        log.info(
                                "Duplicate file upload prevented: userId={}, hash={}",
                                userId, fileHash
                        );
                    } else {
                        log.error("DB save failed, file rolled back", ex);
                    }
                })

                .get();
    }

    @Transactional
    public void transactionalSave(UploadedFileDto uploadedFileDto, String fileHash, Long userId) {
        try {
            FileMetadata fileMetadata = FileMetadataMapper.mapToEntity(uploadedFileDto, fileHash, userId);
            filesMetadataRepository.save(fileMetadata);
        } catch (DataIntegrityViolationException e) {
                throw new DuplicateFileException(userId, fileHash);
        }
    }
}
