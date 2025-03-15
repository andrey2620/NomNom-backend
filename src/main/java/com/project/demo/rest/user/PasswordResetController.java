package com.project.demo.rest.user;

import com.project.demo.logic.entity.user.User;
import com.project.demo.services.EmailService;
import com.project.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String email) {
        // Verifica si el correo existe en la base de datos
        Optional<User> userOptional = userService.findByEmail(email);
        if (!userOptional.isPresent()) {
            return "Correo no encontrado";
        }

        User user = userOptional.get();

        // Genera un token único para la recuperación
        String token = UUID.randomUUID().toString();

        // Guarda el token temporalmente en la base de datos asociado al usuario
        userService.savePasswordResetToken(user, token);

        // Genera el enlace para restablecer la contraseña
        String resetLink = "http://localhost:4200/reset-password?token=" + token;

        // Envia el correo con el enlace
        emailService.sendPasswordResetEmail(user.getEmail(), resetLink);

        return "Correo de recuperación enviado";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        // Verifica si el token es válido y está asociado a un usuario
        Optional<User> userOptional = userService.findByResetToken(token);
        if (!userOptional.isPresent()) {
            return "Token no válido o expirado";
        }

        User user = userOptional.get();
        long currentTime = Instant.now().toEpochMilli();

        // Verifica si el token ha expirado
        if (user.getTokenExpirationTime() < currentTime) {
            return "El token ha expirado";
        }

        // Actualiza la contraseña
        userService.changePassword(user, newPassword);

        return "Contraseña actualizada";
    }
}