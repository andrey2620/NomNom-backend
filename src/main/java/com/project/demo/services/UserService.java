package com.project.demo.services;

import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Buscar un usuario por correo electrónico
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Buscar un usuario por token de recuperación de contraseña
    public Optional<User> findByResetToken(String token) {
        return userRepository.findByPasswordResetToken(token);
    }

    // Guardar un nuevo token de recuperación de contraseña para el usuario
    public void savePasswordResetToken(User user, String token) {
        user.setPasswordResetToken(token);
        user.setTokenExpirationTime(Instant.now().plusSeconds(3600).toEpochMilli());  // Token válido por 1 hora
        userRepository.save(user);
    }

    // Cambiar la contraseña del usuario
    public void changePassword(User user, String newPassword) {
        // Usamos el PasswordEncoder para encriptar la nueva contraseña
        String encryptedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encryptedPassword);
        user.setPasswordResetToken(null);  // Limpiar el token de restablecimiento
        user.setTokenExpirationTime(0);  // Limpiar la expiración del token
        userRepository.save(user);
    }

    public void deleteToken(User user) {
        user.setPasswordResetToken(null);
        user.setTokenExpirationTime(0);
        userRepository.save(user);
    }
}
