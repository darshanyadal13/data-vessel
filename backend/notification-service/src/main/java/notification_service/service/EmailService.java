package notification_service.service;

import notification_service.dto.EmailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.email.from}")
    private String fromEmail;

    public void sendEmail(EmailRequest emailRequest) throws Exception {
        try {
            if (emailRequest.isHtml()) {
                sendHtmlEmail(emailRequest);
            } else {
                sendSimpleEmail(emailRequest);
            }
        } catch (Exception e) {
            throw new Exception("Failed to send email: " + e.getMessage());
        }
    }

    private void sendSimpleEmail(EmailRequest emailRequest) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(emailRequest.getTo());
        message.setSubject(emailRequest.getSubject());
        message.setText(emailRequest.getBody());
        mailSender.send(message);
    }

    private void sendHtmlEmail(EmailRequest emailRequest) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setFrom(fromEmail);
        helper.setTo(emailRequest.getTo());
        helper.setSubject(emailRequest.getSubject());
        helper.setText(emailRequest.getBody(), true);
        mailSender.send(message);
    }

    public void sendWelcomeEmail(String to, String username) throws Exception {
        String subject = "Welcome to Data Vessel!";
        String body = String.format(
            "<html><body>" +
            "<h2>Welcome to Data Vessel, %s!</h2>" +
            "<p>Thank you for registering with us.</p>" +
            "<p>You can now start uploading and managing your files securely.</p>" +
            "<br/>" +
            "<p>Best regards,<br/>Data Vessel Team</p>" +
            "</body></html>",
            username
        );
        EmailRequest request = new EmailRequest(to, subject, body, true);
        sendEmail(request);
    }

    public void sendFileUploadNotification(String to, String filename) throws Exception {
        String subject = "File Uploaded Successfully";
        String body = String.format(
            "<html><body>" +
            "<h3>File Upload Confirmation</h3>" +
            "<p>Your file <strong>%s</strong> has been uploaded successfully.</p>" +
            "<p>You can access it anytime from your Data Vessel dashboard.</p>" +
            "<br/>" +
            "<p>Best regards,<br/>Data Vessel Team</p>" +
            "</body></html>",
            filename
        );
        EmailRequest request = new EmailRequest(to, subject, body, true);
        sendEmail(request);
    }

    public void sendFileSharedNotification(String to, String filename, String sharedBy) throws Exception {
        String subject = "File Shared With You";
        String body = String.format(
            "<html><body>" +
            "<h3>New File Shared</h3>" +
            "<p><strong>%s</strong> has shared a file with you: <strong>%s</strong></p>" +
            "<p>Log in to Data Vessel to access the shared file.</p>" +
            "<br/>" +
            "<p>Best regards,<br/>Data Vessel Team</p>" +
            "</body></html>",
            sharedBy,
            filename
        );
        EmailRequest request = new EmailRequest(to, subject, body, true);
        sendEmail(request);
    }

    public void sendPasswordResetEmail(String to, String resetToken) throws Exception {
        String subject = "Password Reset Request";
        String body = String.format(
            "<html><body>" +
            "<h3>Password Reset</h3>" +
            "<p>You have requested to reset your password.</p>" +
            "<p>Use the following token to reset your password: <strong>%s</strong></p>" +
            "<p>If you did not request this, please ignore this email.</p>" +
            "<br/>" +
            "<p>Best regards,<br/>Data Vessel Team</p>" +
            "</body></html>",
            resetToken
        );
        EmailRequest request = new EmailRequest(to, subject, body, true);
        sendEmail(request);
    }
}
