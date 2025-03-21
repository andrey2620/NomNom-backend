package com.project.demo.rest.user;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.user.User;
import com.project.demo.services.EmailService;
import com.project.demo.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email, HttpServletRequest request) {
        // Verifica si el correo existe en la base de datos
        Optional<User> userOptional = userService.findByEmail(email);
        if (!userOptional.isPresent()) {
            // Usamos el GlobalResponseHandler como en el resto del código
            return new GlobalResponseHandler().handleResponse("Correo no encontrado",
                    HttpStatus.BAD_REQUEST, request);
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

        // Usamos el GlobalResponseHandler para la respuesta exitosa
        return new GlobalResponseHandler().handleResponse("Correo de recuperación enviado",
                HttpStatus.OK, request);
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestBody String newPassword, HttpServletRequest httpRequest) {
        // Verifica si el token es válido y está asociado a un usuario
        Optional<User> userOptional = userService.findByResetToken(token);
        if (!userOptional.isPresent()) {
            return new GlobalResponseHandler().handleResponse("Token no válido o expirado", HttpStatus.BAD_REQUEST, httpRequest);
        }

        User user = userOptional.get();
        long currentTime = Instant.now().toEpochMilli();

        // Verifica si el token ha expirado
        if (user.getTokenExpirationTime() < currentTime) {
            return new GlobalResponseHandler().handleResponse("El token ha expirado", HttpStatus.BAD_REQUEST, httpRequest);
        }

        // Actualiza la contraseña
        userService.changePassword(user, newPassword);

        return new GlobalResponseHandler().handleResponse("Contraseña actualizada", HttpStatus.OK, httpRequest);
    }

}