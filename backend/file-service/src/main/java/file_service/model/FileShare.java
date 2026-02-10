package file_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "file_shares")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileShare {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long fileId;
    
    @Column(nullable = false)
    private Long sharedWithUserId;
    
    @Column(nullable = false)
    private String permission; // READ, WRITE, DELETE
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime sharedAt;
    
    @PrePersist
    protected void onCreate() {
        sharedAt = LocalDateTime.now();
    }
}
