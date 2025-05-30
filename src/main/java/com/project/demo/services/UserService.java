package com.project.demo.services;

import com.project.demo.logic.entity.recipe.Recipe;
import com.project.demo.logic.entity.recipe.RecipeRepository;
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
    private RecipeRepository recipeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findByResetToken(String token) {
        return userRepository.findByPasswordResetToken(token);
    }

    public void savePasswordResetToken(User user, String token) {
        user.setPasswordResetToken(token);
        user.setTokenExpirationTime(Instant.now().plusSeconds(3600).toEpochMilli());
        userRepository.save(user);
    }

    public void changePassword(User user, String newPassword) {
        String encryptedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encryptedPassword);
        user.setPasswordResetToken(null);
        user.setTokenExpirationTime(0);
        userRepository.save(user);
    }

    public void deleteToken(User user) {
        user.setPasswordResetToken(null);
        user.setTokenExpirationTime(0);
        userRepository.save(user);
    }


    public void addRecipeToFavorites(Long userId, Long recipeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada"));

        user.addFavoriteRecipe(recipe);
        userRepository.save(user);
    }

    public Recipe saveRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }


}
