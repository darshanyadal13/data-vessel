package file_service.repository;

import file_service.model.Folder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByOwnerId(Long ownerId);
    List<Folder> findByOwnerIdAndParentId(Long ownerId, Long parentId);
}
