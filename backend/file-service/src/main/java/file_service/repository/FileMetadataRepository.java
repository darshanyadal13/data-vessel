package file_service.repository;

import file_service.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    List<FileMetadata> findByOwnerIdAndDeletedFalse(Long ownerId);
    List<FileMetadata> findByOwnerIdAndDeletedTrue(Long ownerId);
    List<FileMetadata> findByOwnerIdAndFolderIdAndDeletedFalse(Long ownerId, Long folderId);
    List<FileMetadata> findByOwnerIdAndFilenameContainingIgnoreCaseAndDeletedFalse(Long ownerId, String filename);
}
