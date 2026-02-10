package admin_service.service;

import admin_service.dto.SystemMetrics;
import admin_service.model.AuditLog;
import admin_service.model.Users;
import admin_service.repository.AuditLogRepository;
import admin_service.repository.UserRepository;
import admin_service.util.ResponseStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public ResponseStructure<List<Users>> getAllUsers() {
        List<Users> users = userRepository.findAll();
        users.forEach(user -> user.setPassword(null)); // Don't send passwords
        
        ResponseStructure<List<Users>> response = new ResponseStructure<>();
        response.setData(users);
        response.setMessage("Users retrieved successfully");
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }

    public ResponseStructure<String> deleteUser(Integer userId) {
        ResponseStructure<String> response = new ResponseStructure<>();
        Optional<Users> userOpt = userRepository.findById(userId);
        
        if (userOpt.isEmpty()) {
            response.setMessage("User not found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return response;
        }
        
        userRepository.deleteById(userId);
        
        // Log the action
        logAction(userId.longValue(), "DELETE_USER", "User deleted by admin", "system");
        
        response.setMessage("User deleted successfully");
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }

    public ResponseStructure<String> deactivateUser(Integer userId) {
        ResponseStructure<String> response = new ResponseStructure<>();
        Optional<Users> userOpt = userRepository.findById(userId);
        
        if (userOpt.isEmpty()) {
            response.setMessage("User not found");
            response.setStatusCode(HttpStatus.NOT_FOUND.value());
            return response;
        }
        
        Users user = userOpt.get();
        user.setActive(false);
        userRepository.save(user);
        
        // Log the action
        logAction(userId.longValue(), "DEACTIVATE_USER", "User deactivated by admin", "system");
        
        response.setMessage("User deactivated successfully");
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }

    public ResponseStructure<SystemMetrics> getSystemMetrics() {
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.findAll().stream().filter(Users::isActive).count();
        
        // In a real implementation, you would call file-service to get file metrics
        // For now, using placeholder values
        long totalFiles = 0;
        long totalStorageUsed = 0;
        
        SystemMetrics metrics = new SystemMetrics(
            totalUsers,
            activeUsers,
            totalFiles,
            totalStorageUsed,
            "MB"
        );
        
        ResponseStructure<SystemMetrics> response = new ResponseStructure<>();
        response.setData(metrics);
        response.setMessage("Metrics retrieved successfully");
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }

    public ResponseStructure<List<AuditLog>> getAuditLogs() {
        List<AuditLog> logs = auditLogRepository.findAll();
        
        ResponseStructure<List<AuditLog>> response = new ResponseStructure<>();
        response.setData(logs);
        response.setMessage("Audit logs retrieved successfully");
        response.setStatusCode(HttpStatus.OK.value());
        return response;
    }

    private void logAction(Long userId, String action, String details, String ipAddress) {
        AuditLog log = new AuditLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setDetails(details);
        log.setIpAddress(ipAddress);
        auditLogRepository.save(log);
    }
}
