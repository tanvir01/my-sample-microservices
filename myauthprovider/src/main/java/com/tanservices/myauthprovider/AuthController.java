package com.tanservices.myauthprovider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        // Validate username and password
        String username = request.username();
        String password = request.password();

        Optional<User> user = userService.findByUsername(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        if (!passwordEncoder.matches(password, user.get().getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        // Generate JWT token
        String jwtToken = jwtService.generateToken(user.get());

        System.out.println(jwtService.getUserInfoFromToken(jwtToken));

        // Return response
        AuthResponse response = new AuthResponse(jwtToken);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id).get());
    }
}

