package user_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import user_service.dto.LoginRequest;
import user_service.model.Users;
import user_service.repository.UserRepository;
import user_service.util.ResponseStructure;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JWTService jwtService;

    public ResponseStructure<Users> register(Users user) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setActive(true); // Set user as active by default
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER"); // Default role
        }
        userRepo.save(user);
        ResponseStructure<Users> rs = new ResponseStructure<Users>();
        // Don't send password in response
        user.setPassword(null);
        rs.setData(user);
        rs.setMessage("User Registered");
        rs.setStatusCode(HttpStatus.CREATED.value());
        return rs;
    }

    public String login(LoginRequest loginRequest){
        Authentication authentication = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        if(authentication.isAuthenticated()){
            return jwtService.getToken(loginRequest.getUsername());
        } else {
            throw new RuntimeException("Authentication failed");
        }
    }

    public ResponseStructure<Users> getUserProfile(String username) {
        Users user = userRepo.findUserByUsername(username);
        ResponseStructure<Users> rs = new ResponseStructure<>();
        if (user != null) {
            user.setPassword(null); // Don't send password
            rs.setData(user);
            rs.setMessage("User profile fetched successfully");
            rs.setStatusCode(HttpStatus.OK.value());
        } else {
            rs.setMessage("User not found");
            rs.setStatusCode(HttpStatus.NOT_FOUND.value());
        }
        return rs;
    }

    public ResponseStructure<Users> updateUserProfile(String username, Users updatedUser) {
        Users user = userRepo.findUserByUsername(username);
        ResponseStructure<Users> rs = new ResponseStructure<>();
        if (user != null) {
            // Update only allowed fields
            if (updatedUser.getEmail() != null) {
                user.setEmail(updatedUser.getEmail());
            }
            // Don't allow username or role update through this endpoint
            userRepo.save(user);
            user.setPassword(null); // Don't send password
            rs.setData(user);
            rs.setMessage("Profile updated successfully");
            rs.setStatusCode(HttpStatus.OK.value());
        } else {
            rs.setMessage("User not found");
            rs.setStatusCode(HttpStatus.NOT_FOUND.value());
        }
        return rs;
    }
}
