package user_service.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import user_service.dto.LoginRequest;
import user_service.model.Users;
import user_service.service.JWTService;
import user_service.service.UserService;
import user_service.util.ResponseStructure;

@RequestMapping("/api")
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JWTService jwtService;

    @PostMapping("/auth/register")
    public ResponseEntity<ResponseStructure<Users>> register(@RequestBody Users user) {
         ResponseStructure<Users> rs = userService.register(user);
         return new ResponseEntity<>(rs, HttpStatus.CREATED);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        String token = userService.login(loginRequest);
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("message", "Login successful");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        // In a stateless JWT system, logout is typically handled client-side
        // Here we can add token to blacklist if needed
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout successful");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/profile")
    public ResponseEntity<ResponseStructure<Users>> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        ResponseStructure<Users> rs = userService.getUserProfile(username);
        return ResponseEntity.ok(rs);
    }

    @PutMapping("/user/profile")
    public ResponseEntity<ResponseStructure<Users>> updateProfile(@RequestBody Users user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        ResponseStructure<Users> rs = userService.updateUserProfile(username, user);
        return ResponseEntity.ok(rs);
    }
}
