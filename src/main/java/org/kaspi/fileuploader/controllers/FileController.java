package org.kaspi.fileuploader.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kaspi.fileuploader.domain.dto.FileRequestDto;
import org.kaspi.fileuploader.services.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/files")
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<Void> uploadFile(@ModelAttribute FileRequestDto fileRequestDto) {
        Instant start = Instant.now();

        fileService.createAndSave(fileRequestDto);

        Instant end = Instant.now();
        log.info("Creating and saving FileMetadata with file: {} took {} seconds", fileRequestDto.getFile().getOriginalFilename(), Duration.between(start, end).toSeconds());
        return ResponseEntity.accepted().build();
    }
}
