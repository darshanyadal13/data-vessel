package file_service.controller;

import file_service.dto.*;
import file_service.model.FileMetadata;
import file_service.model.Folder;
import file_service.service.FileService;
import file_service.util.ResponseStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    private Long getCurrentUserId() {
        // In a real implementation, this would extract user ID from JWT token
        // For now, we'll use a placeholder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // You would typically extract userId from the token or make a call to user-service
        return 1L; // Placeholder
    }

    @PostMapping("/upload")
    public ResponseEntity<ResponseStructure<FileUploadResponse>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folderId", required = false) Long folderId,
            @RequestParam(value = "tags", required = false) String tags) {
        
        Long userId = getCurrentUserId();
        ResponseStructure<FileUploadResponse> response = fileService.uploadFile(file, userId, folderId, tags);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            InputStream fileStream = fileService.downloadFile(id, userId);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
                    .body(new InputStreamResource(fileStream));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping
    public ResponseEntity<ResponseStructure<List<FileMetadata>>> listFiles() {
        Long userId = getCurrentUserId();
        ResponseStructure<List<FileMetadata>> response = fileService.listFiles(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ResponseStructure<List<FileMetadata>>> searchFiles(@RequestParam String query) {
        Long userId = getCurrentUserId();
        ResponseStructure<List<FileMetadata>> response = fileService.searchFiles(userId, query);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/folder/create")
    public ResponseEntity<ResponseStructure<Folder>> createFolder(@RequestBody FolderCreateRequest request) {
        Long userId = getCurrentUserId();
        ResponseStructure<Folder> response = fileService.createFolder(request, userId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @DeleteMapping("/folder/{id}")
    public ResponseEntity<ResponseStructure<String>> deleteFolder(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        ResponseStructure<String> response = fileService.deleteFolder(id, userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/rename/{id}")
    public ResponseEntity<ResponseStructure<String>> renameFile(
            @PathVariable Long id,
            @RequestParam String newName) {
        Long userId = getCurrentUserId();
        ResponseStructure<String> response = fileService.renameFile(id, newName, userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/move")
    public ResponseEntity<ResponseStructure<String>> moveFile(@RequestBody FileMoveRequest request) {
        Long userId = getCurrentUserId();
        ResponseStructure<String> response = fileService.moveFile(request, userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseStructure<String>> deleteFile(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        ResponseStructure<String> response = fileService.deleteFile(id, userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/trash")
    public ResponseEntity<ResponseStructure<List<FileMetadata>>> getTrash() {
        Long userId = getCurrentUserId();
        ResponseStructure<List<FileMetadata>> response = fileService.getTrash(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/restore/{id}")
    public ResponseEntity<ResponseStructure<String>> restoreFile(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        ResponseStructure<String> response = fileService.restoreFile(id, userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/permanent-delete/{id}")
    public ResponseEntity<ResponseStructure<String>> permanentDelete(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        ResponseStructure<String> response = fileService.permanentDelete(id, userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/share")
    public ResponseEntity<ResponseStructure<String>> shareFile(@RequestBody FileShareRequest request) {
        Long userId = getCurrentUserId();
        ResponseStructure<String> response = fileService.shareFile(request, userId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
