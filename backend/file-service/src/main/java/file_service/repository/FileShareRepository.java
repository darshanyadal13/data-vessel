package file_service.repository;

import file_service.model.FileShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileShareRepository extends JpaRepository<FileShare, Long> {
    List<FileShare> findByFileId(Long fileId);
    List<FileShare> findBySharedWithUserId(Long userId);
}
