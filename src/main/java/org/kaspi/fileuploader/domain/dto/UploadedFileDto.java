package org.kaspi.fileuploader.domain.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadedFileDto {
    private String originalFileName;
    private long fileSize;
    private String fileType;
    private String bucket;
    private String storageKey;
}
