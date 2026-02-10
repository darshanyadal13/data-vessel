package notification_service.controller;

import notification_service.dto.EmailRequest;
import notification_service.service.EmailService;
import notification_service.util.ResponseStructure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notify")
public class NotificationController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/email")
    public ResponseEntity<ResponseStructure<String>> sendEmail(@RequestBody EmailRequest emailRequest) {
        ResponseStructure<String> response = new ResponseStructure<>();
        try {
            emailService.sendEmail(emailRequest);
            response.setMessage("Email sent successfully");
            response.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage("Failed to send email: " + e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/welcome")
    public ResponseEntity<ResponseStructure<String>> sendWelcomeEmail(
            @RequestParam String email,
            @RequestParam String username) {
        ResponseStructure<String> response = new ResponseStructure<>();
        try {
            emailService.sendWelcomeEmail(email, username);
            response.setMessage("Welcome email sent successfully");
            response.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage("Failed to send welcome email: " + e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/file-upload")
    public ResponseEntity<ResponseStructure<String>> sendFileUploadNotification(
            @RequestParam String email,
            @RequestParam String filename) {
        ResponseStructure<String> response = new ResponseStructure<>();
        try {
            emailService.sendFileUploadNotification(email, filename);
            response.setMessage("File upload notification sent successfully");
            response.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage("Failed to send notification: " + e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/file-shared")
    public ResponseEntity<ResponseStructure<String>> sendFileSharedNotification(
            @RequestParam String email,
            @RequestParam String filename,
            @RequestParam String sharedBy) {
        ResponseStructure<String> response = new ResponseStructure<>();
        try {
            emailService.sendFileSharedNotification(email, filename, sharedBy);
            response.setMessage("File shared notification sent successfully");
            response.setStatusCode(HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setMessage("Failed to send notification: " + e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
