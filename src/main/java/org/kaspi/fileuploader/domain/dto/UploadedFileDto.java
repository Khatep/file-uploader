package org.kaspi.fileuploader.domain.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadedFileDto {
    private String originalFileName;
    private String storedFileName;
    private long fileSize;
    private String fileType;
    //private FileStorageType fileStorageType;
    private String remotePath;
}
