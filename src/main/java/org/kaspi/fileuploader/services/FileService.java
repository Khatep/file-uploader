package org.kaspi.fileuploader.services;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kaspi.fileuploader.domain.dto.FileRequestDto;
import org.kaspi.fileuploader.domain.dto.UploadedFileDto;
import org.kaspi.fileuploader.domain.models.FileMetadata;
import org.kaspi.fileuploader.domain.repositories.FilesMetadataRepository;
import org.kaspi.fileuploader.utils.TempFileUtils;
import org.kaspi.fileuploader.utils.mappers.FileMetadataMapper;
import org.springframework.beans.factory.ObjectProvider;
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
        //String userId = userService.findByUsername(dto.getUserId());

        try {
            // Сохраняем MultipartFile во временный файл
            File tempFile = TempFileUtils.saveTempFile(dto);

            CompletableFuture
                    .supplyAsync(() -> fileStorageService.upload(tempFile), taskExecutor)
                    .thenAccept(this::saveWithCompensation)
                    .whenComplete((res, ex) -> {
                        TempFileUtils.deleteFile(tempFile);
                        //TODO: KAFKA FOR PUSH
                    })
                    .exceptionally(ex -> {
                        TempFileUtils.deleteFile(tempFile);
                        log.error("Failed to process proof of address", ex);
                        //TODO: KAFKA for PUSH
                        return null;
                    });
        } catch (Exception e) {
            log.error("Failed to process proof of address", e);
        }
    }

    private void saveWithCompensation(UploadedFileDto document) {
        Try.run(() -> selfProvider.getObject().transactionalSave(document))
                .onFailure(ex -> {
                    fileStorageService.delete(document.getStorageKey());
                    log.error("DB save failed, file rolled back", ex);
                })
                .get();
    }

    @Transactional
    public void transactionalSave(UploadedFileDto uploadedFileDto) {
        FileMetadata fileMetadata = FileMetadataMapper.mapToEntity(uploadedFileDto);
        filesMetadataRepository.save(fileMetadata);
    }
}
