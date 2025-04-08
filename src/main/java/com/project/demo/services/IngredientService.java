package com.project.demo.services;

import com.project.demo.logic.entity.ingredient.Ingredient;
import com.project.demo.logic.entity.ingredient.IngredientRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IngredientService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IngredientRepository ingredientRepository;

    public Optional<Ingredient> getIngredientById(Long id) {
        return ingredientRepository.findById(id);
    }

    @Transactional
    public List<String> getFormattedIngredientsByUserId(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        User user = userOptional.get();

        return user.getIngredients().stream()
                .map(Ingredient::getName)
                .collect(Collectors.toList());
    }



    public Ingredient saveIngredient(Ingredient ingredient) {
        return ingredientRepository.save(ingredient);
    }

    public Ingredient createIngredientAndAssignToUser(Ingredient ingredient, Long userId) {
        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.getIngredients().add(savedIngredient);
            userRepository.save(user);
        }
        return savedIngredient;
    }

    public String linkExistingIngredientToUser(Long ingredientId, Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        Optional<Ingredient> ingredientOptional = ingredientRepository.findById(ingredientId);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Usuario con id " + userId + " no existe");
        }

        if (ingredientOptional.isEmpty()) {
            throw new IllegalArgumentException("Ingrediente con id " + ingredientId + " no existe");
        }

        User user = userOptional.get();
        Ingredient ingredient = ingredientOptional.get();

        if (user.getIngredients().contains(ingredient)) {
            return "El ingrediente ya está vinculado a este usuario.";
        }

        user.getIngredients().add(ingredient);
        userRepository.save(user);
        return "Ingrediente vinculado correctamente al usuario.";
    }

    public Map<Long, String> bulkLinkIngredientsToUser(List<Long> ingredientIds, Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("Usuario con id " + userId + " no existe");
        }

        User user = userOptional.get();
        Map<Long, String> resultMessages = new HashMap<>();

        for (Long ingredientId : ingredientIds) {
            Optional<Ingredient> ingredientOptional = ingredientRepository.findById(ingredientId);

            if (ingredientOptional.isEmpty()) {
                resultMessages.put(ingredientId, "Ingrediente no encontrado");
                continue;
            }

            Ingredient ingredient = ingredientOptional.get();

            if (user.getIngredients().contains(ingredient)) {
                resultMessages.put(ingredientId, "Ya está vinculado");
            } else {
                user.getIngredients().add(ingredient);
                resultMessages.put(ingredientId, "Vinculado correctamente");
            }
        }

        userRepository.save(user);
        return resultMessages;
    }


    public void deleteIngredient(Long id) {
        ingredientRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return ingredientRepository.existsById(id);
    }

    public Page<Ingredient> getAllIngredients(Pageable pageable) {
        return ingredientRepository.findAll(pageable);
    }
}