package org.kaspi.fileuploader.utils.mappers;

import lombok.experimental.UtilityClass;
import org.kaspi.fileuploader.domain.dto.UploadedFileDto;
import org.kaspi.fileuploader.domain.models.FileMetadata;

@UtilityClass
public class FileMetadataMapper {
    public static FileMetadata mapToEntity(UploadedFileDto uploadedDocumentDto, String fileHash, Long userId) {
        return FileMetadata.builder()
                .userId(userId)
                .fileName(uploadedDocumentDto.getOriginalFileName())
                .fileSize(uploadedDocumentDto.getFileSize())
                .fileType(uploadedDocumentDto.getFileType())
                .fileHash(fileHash)
                .bucket(uploadedDocumentDto.getBucket())
                .storageKey(uploadedDocumentDto.getStorageKey())
                .build();
    }
}
