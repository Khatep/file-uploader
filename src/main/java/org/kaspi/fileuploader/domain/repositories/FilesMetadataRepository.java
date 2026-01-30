package org.kaspi.fileuploader.domain.repositories;

import org.kaspi.fileuploader.domain.models.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FilesMetadataRepository extends JpaRepository<FileMetadata, Long> {

}
