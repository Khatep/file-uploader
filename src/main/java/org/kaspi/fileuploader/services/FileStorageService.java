package org.kaspi.fileuploader.services;

import org.kaspi.fileuploader.domain.dto.FileRequestDto;
import org.kaspi.fileuploader.domain.dto.UploadedFileDto;

import java.io.File;
import java.io.InputStream;

public interface FileStorageService {
    UploadedFileDto upload(File file);

    void delete(String storageKey);

    InputStream download(String storageKey);
}
