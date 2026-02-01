package org.kaspi.fileuploader.utils.mappers;

import lombok.experimental.UtilityClass;
import org.kaspi.fileuploader.domain.dto.UploadedFileDto;
import org.kaspi.fileuploader.domain.models.FileMetadata;

@UtilityClass
public class FileMetadataMapper {
    public static FileMetadata mapToEntity(UploadedFileDto uploadedDocumentDto) {
        return FileMetadata.builder()
                .fileName(uploadedDocumentDto.getOriginalFileName())
                .fileSize(uploadedDocumentDto.getFileSize())
                .fileType(uploadedDocumentDto.getFileType())
                .bucket(uploadedDocumentDto.getBucket())
                .storageKey(uploadedDocumentDto.getStorageKey())
                .build();
    }
}
