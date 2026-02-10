package file_service.service;

import file_service.dto.*;
import file_service.model.FileMetadata;
import file_service.model.FileShare;
import file_service.model.Folder;
import file_service.repository.FileMetadataRepository;
import file_service.repository.FileShareRepository;
import file_service.repository.FolderRepository;
import file_service.util.ResponseStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {

    @Autowired
    private FileMetadataRepository fileMetadataRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FileShareRepository fileShareRepository;

    @Autowired
    private S3Service s3Service;

    public ResponseStructure<FileUploadResponse> uploadFile(MultipartFile file, Long userId, Long folderId, String tags) {
        ResponseStructure<FileUploadResponse> response = new ResponseStructure<>();
        try {
            String s3Url = s3Service.uploadFile(file, "user_" + userId);
            
            FileMetadata metadata = new FileMetadata();
            metadata.setFilename(file.getOriginalFilename());
            metadata.setOriginalFilename(file.getOriginalFilename());
            metadata.setOwnerId(userId);
            metadata.setSize(file.getSize());
            metadata.setFileType(file.getContentType());
            metadata.setS3Url(s3Url);
            metadata.setS3Key(extractKeyFromUrl(s3Url));
            metadata.setFolderId(folderId);
            metadata.setTags(tags);
            
            fileMetadataRepository.save(metadata);
            
            FileUploadResponse uploadResponse = new FileUploadResponse(
                metadata.getId(),
                metadata.getFilename(),
                "File uploaded successfully",
                s3Url,
                metadata.getSize()
            );
            
            response.setData(uploadResponse);
            response.setMessage("File uploaded successfully");
            response.setStatusCode(HttpStatus.CREATED.value());
        } catch (Exception e) {
            response.setMessage("File upload failed: " + e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    public InputStream downloadFile(Long fileId, Long userId) throws Exception {
        Optional<FileMetadata> fileOpt = fileMetadataRepository.findById(fileId);
        if (fileOpt.isEmpty()) {
            throw new Exception("File not found");
        }
        
        FileMetadata file = fileOpt.get();
        if (!file.getOwnerId().equals(userId) && !hasAccessToFile(fileId, userId)) {
            throw new Exception("Access denied");
        }
        
        return s3Service.downloadFile(file.getS3Key());
    }

    public ResponseStructure<List<FileMetadata>> listFiles(Long userId) {
        List<FileMetadata> files = fileMetadataRepository.findByOwnerIdAndDeletedFalse(userId);
        ResponseStructure<List<FileMetadata>> response = new ResponseStructure<>();
        response.setData(files);
        response.setMessage("Files retrieved successfully");
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }

    public ResponseStructure<List<FileMetadata>> searchFiles(Long userId, String query) {
        List<FileMetadata> files = fileMetadataRepository.findByOwnerIdAndFilenameContainingIgnoreCaseAndDeletedFalse(userId, query);
        ResponseStructure<List<FileMetadata>> response = new ResponseStructure<>();
        response.setData(files);
        response.setMessage("Search completed");
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }

    public ResponseStructure<Folder> createFolder(FolderCreateRequest request, Long userId) {
        Folder folder = new Folder();
        folder.setName(request.getName());
        folder.setOwnerId(userId);
        folder.setParentId(request.getParentId());
        folderRepository.save(folder);
        
        ResponseStructure<Folder> response = new ResponseStructure<>();
        response.setData(folder);
        response.setMessage("Folder created successfully");
        response.setStatusCode(HttpStatus.CREATED.value());
        return response;
    }

    public ResponseStructure<String> renameFile(Long fileId, String newName, Long userId) {
        ResponseStructure<String> response = new ResponseStructure<>();
        Optional<FileMetadata> fileOpt = fileMetadataRepository.findById(fileId);
        
        if (fileOpt.isEmpty()) {
            response.setMessage("File not found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return response;
        }
        
        FileMetadata file = fileOpt.get();
        if (!file.getOwnerId().equals(userId)) {
            response.setMessage("Access denied");
            response.setStatusCode(HttpStatus.FORBIDDEN.value());
            return response;
        }
        
        file.setFilename(newName);
        fileMetadataRepository.save(file);
        
        response.setData(newName);
        response.setMessage("File renamed successfully");
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }

    public ResponseStructure<String> moveFile(FileMoveRequest request, Long userId) {
        ResponseStructure<String> response = new ResponseStructure<>();
        Optional<FileMetadata> fileOpt = fileMetadataRepository.findById(request.getFileId());
        
        if (fileOpt.isEmpty()) {
            response.setMessage("File not found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return response;
        }
        
        FileMetadata file = fileOpt.get();
        if (!file.getOwnerId().equals(userId)) {
            response.setMessage("Access denied");
            response.setStatusCode(HttpStatus.FORBIDDEN.value());
            return response;
        }
        
        file.setFolderId(request.getTargetFolderId());
        fileMetadataRepository.save(file);
        
        response.setMessage("File moved successfully");
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }

    public ResponseStructure<String> deleteFile(Long fileId, Long userId) {
        ResponseStructure<String> response = new ResponseStructure<>();
        Optional<FileMetadata> fileOpt = fileMetadataRepository.findById(fileId);
        
        if (fileOpt.isEmpty()) {
            response.setMessage("File not found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return response;
        }
        
        FileMetadata file = fileOpt.get();
        if (!file.getOwnerId().equals(userId)) {
            response.setMessage("Access denied");
            response.setStatusCode(HttpStatus.FORBIDDEN.value());
            return response;
        }
        
        file.setDeleted(true);
        fileMetadataRepository.save(file);
        
        response.setMessage("File moved to trash");
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }

    public ResponseStructure<List<FileMetadata>> getTrash(Long userId) {
        List<FileMetadata> files = fileMetadataRepository.findByOwnerIdAndDeletedTrue(userId);
        ResponseStructure<List<FileMetadata>> response = new ResponseStructure<>();
        response.setData(files);
        response.setMessage("Trash files retrieved");
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }

    public ResponseStructure<String> restoreFile(Long fileId, Long userId) {
        ResponseStructure<String> response = new ResponseStructure<>();
        Optional<FileMetadata> fileOpt = fileMetadataRepository.findById(fileId);
        
        if (fileOpt.isEmpty()) {
            response.setMessage("File not found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return response;
        }
        
        FileMetadata file = fileOpt.get();
        if (!file.getOwnerId().equals(userId)) {
            response.setMessage("Access denied");
            response.setStatusCode(HttpStatus.FORBIDDEN.value());
            return response;
        }
        
        file.setDeleted(false);
        fileMetadataRepository.save(file);
        
        response.setMessage("File restored successfully");
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }

    public ResponseStructure<String> permanentDelete(Long fileId, Long userId) {
        ResponseStructure<String> response = new ResponseStructure<>();
        Optional<FileMetadata> fileOpt = fileMetadataRepository.findById(fileId);
        
        if (fileOpt.isEmpty()) {
            response.setMessage("File not found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return response;
        }
        
        FileMetadata file = fileOpt.get();
        if (!file.getOwnerId().equals(userId)) {
            response.setMessage("Access denied");
            response.setStatusCode(HttpStatus.FORBIDDEN.value());
            return response;
        }
        
        try {
            s3Service.deleteFile(file.getS3Key());
            fileMetadataRepository.delete(file);
            
            response.setMessage("File permanently deleted");
            response.setStatusCode(HttpStatus.OK.value());
        } catch (Exception e) {
            response.setMessage("Failed to delete file: " + e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return response;
    }

    public ResponseStructure<String> shareFile(FileShareRequest request, Long userId) {
        ResponseStructure<String> response = new ResponseStructure<>();
        Optional<FileMetadata> fileOpt = fileMetadataRepository.findById(request.getFileId());
        
        if (fileOpt.isEmpty()) {
            response.setMessage("File not found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return response;
        }
        
        FileMetadata file = fileOpt.get();
        if (!file.getOwnerId().equals(userId)) {
            response.setMessage("Access denied");
            response.setStatusCode(HttpStatus.FORBIDDEN.value());
            return response;
        }
        
        FileShare share = new FileShare();
        share.setFileId(request.getFileId());
        share.setSharedWithUserId(request.getSharedWithUserId());
        share.setPermission(request.getPermission());
        fileShareRepository.save(share);
        
        response.setMessage("File shared successfully");
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }

    private boolean hasAccessToFile(Long fileId, Long userId) {
        List<FileShare> shares = fileShareRepository.findByFileId(fileId);
        return shares.stream().anyMatch(share -> share.getSharedWithUserId().equals(userId));
    }

    private String extractKeyFromUrl(String url) {
        // Extract the S3 key from the full URL
        String[] parts = url.split(".com/");
        return parts.length > 1 ? parts[1] : url;
    }
}
