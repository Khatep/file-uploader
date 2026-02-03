package org.kaspi.fileuploader.domain.exceptions;

import lombok.Getter;

@Getter
public class DuplicateFileException extends RuntimeException {

    private final Long userId;
    private final String fileHash;

    public DuplicateFileException(Long userId, String fileHash) {
        super(buildMessage(userId, fileHash));
        this.userId = userId;
        this.fileHash = fileHash;
    }

    private static String buildMessage(Long userId, String fileHash) {
        return String.format(
                "Duplicate file upload detected. userId=%d, fileHash=%s",
                userId, fileHash
        );
    }
}
