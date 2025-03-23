package com.project.demo.services;

import com.project.demo.logic.entity.ingredient.Ingredient;
import com.project.demo.logic.entity.ingredient.IngredientRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

@Service
public class IngredientService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IngredientRepository ingredientRepository;

    public List<Ingredient> getIngredientsByUserId(Long userId) {
        return ingredientRepository.findByCreatedBy_Id(userId);
    }

    public Optional<Ingredient> getIngredientById(Long id) {
        return ingredientRepository.findById(id);
    }

    public Ingredient saveIngredient(Ingredient ingredient) {
        if (ingredient.getCreatedBy() != null && ingredient.getCreatedBy().getId() != null) {
            User user = userRepository.findById(ingredient.getCreatedBy().getId()).orElseThrow();
            ingredient.setCreatedBy(user); // carga el usuario con su rol
        }
        return ingredientRepository.save(ingredient);
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