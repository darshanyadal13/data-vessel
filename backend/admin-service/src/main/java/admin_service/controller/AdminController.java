package admin_service.controller;

import admin_service.dto.SystemMetrics;
import admin_service.model.AuditLog;
import admin_service.model.Users;
import admin_service.service.AdminService;
import admin_service.util.ResponseStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<ResponseStructure<List<Users>>> getAllUsers() {
        ResponseStructure<List<Users>> response = adminService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<ResponseStructure<String>> deleteUser(@PathVariable Integer id) {
        ResponseStructure<String> response = adminService.deleteUser(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PutMapping("/user/{id}/deactivate")
    public ResponseEntity<ResponseStructure<String>> deactivateUser(@PathVariable Integer id) {
        ResponseStructure<String> response = adminService.deactivateUser(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/metrics")
    public ResponseEntity<ResponseStructure<SystemMetrics>> getMetrics() {
        ResponseStructure<SystemMetrics> response = adminService.getSystemMetrics();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/logs")
    public ResponseEntity<ResponseStructure<List<AuditLog>>> getAuditLogs() {
        ResponseStructure<List<AuditLog>> response = adminService.getAuditLogs();
        return ResponseEntity.ok(response);
    }
}
