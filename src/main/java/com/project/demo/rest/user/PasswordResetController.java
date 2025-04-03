package com.project.demo.rest.user;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.user.User;
import com.project.demo.services.EmailService;
import com.project.demo.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class PasswordResetController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Value("${app.base-url-frontend:http://localhost:4200}")
    private String baseUrlFrontend;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email, HttpServletRequest request) {
        Optional<User> userOptional = userService.findByEmail(email);
        if (!userOptional.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Correo no encontrado",
                    HttpStatus.BAD_REQUEST, request);
        }

        User user = userOptional.get();

        String token = UUID.randomUUID().toString();

        userService.savePasswordResetToken(user, token);

        String resetLink = baseUrlFrontend + "/reset-password?token=" + token;

        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);

        return new GlobalResponseHandler().handleResponse("Correo de recuperación enviado",
                HttpStatus.OK, request);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestBody String newPassword, HttpServletRequest httpRequest) {
        Optional<User> userOptional = userService.findByResetToken(token);
        if (!userOptional.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Token no válido o expirado", HttpStatus.BAD_REQUEST, httpRequest);
        }

        User user = userOptional.get();
        long currentTime = Instant.now().toEpochMilli();

        if (user.getTokenExpirationTime() < currentTime) {
            return new GlobalResponseHandler().handleResponse("El token ha expirado", HttpStatus.BAD_REQUEST, httpRequest);
        }

        userService.changePassword(user, newPassword);

        return new GlobalResponseHandler().handleResponse("Contraseña actualizada", HttpStatus.OK, httpRequest);
    }
}