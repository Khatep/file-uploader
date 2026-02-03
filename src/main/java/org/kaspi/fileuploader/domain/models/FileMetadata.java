package org.kaspi.fileuploader.domain.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
        name = "files_metadata",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_file_hash",
                        columnNames = {"user_id", "file_hash"}
                )
        }

)
public class FileMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "file_name")
    String fileName;

    @Column(name = "file_size")
    Long fileSize;

    @Column(name = "file_type")
    String fileType;

    @Column(name = "file_hash", nullable = false, length = 64)
    private String fileHash;

    @Column(name = "bucket")
    String bucket;

    @Column(name = "storage_key")
    String storageKey;
}

