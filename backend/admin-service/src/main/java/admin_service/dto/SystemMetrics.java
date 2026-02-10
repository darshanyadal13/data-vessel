package admin_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SystemMetrics {
    private long totalUsers;
    private long activeUsers;
    private long totalFiles;
    private long totalStorageUsed;
    private String storageUnit;
}
