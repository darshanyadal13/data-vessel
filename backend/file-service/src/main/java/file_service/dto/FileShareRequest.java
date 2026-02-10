package file_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileShareRequest {
    private Long fileId;
    private Long sharedWithUserId;
    private String permission; // READ, WRITE, DELETE
}
