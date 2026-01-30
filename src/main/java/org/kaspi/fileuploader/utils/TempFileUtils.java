package org.kaspi.fileuploader.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.kaspi.fileuploader.domain.dto.FileRequestDto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

@Slf4j
@UtilityClass
public class TempFileUtils {
    public static File saveTempFile(FileRequestDto dto) throws IOException {
        if (Objects.isNull(dto.getFile())) {
            throw new FileNotFoundException("Multipart file not found.");
        }

        String originalFileName = dto.getFile().getOriginalFilename();
        if (originalFileName == null || !originalFileName.contains(".")) {
            throw new IllegalArgumentException("Invalid original filename: " + originalFileName);
        }

        String extension = originalFileName.substring(originalFileName.lastIndexOf('.'));
        File tempFile = File.createTempFile("upload-", extension);
        dto.getFile().transferTo(tempFile);
        log.info("Temp file saved to {}", tempFile.getAbsolutePath());
        return tempFile;
    }

    public static void deleteFile(File tempFile) {
        if (tempFile.exists()) {
            try {
                Files.delete(tempFile.toPath());
            } catch (IOException e) {
                log.warn("Failed to delete temp file: {}", tempFile.getAbsolutePath(), e);
            }
        }
    }
}
