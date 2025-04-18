package com.project.demo.services;

import com.project.demo.logic.entity.user_recipe.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRecipeService {

    @Autowired
    private UserRecipeRepository userRecipeRepository;

    public UserRecipe save(Long userId, Long recipeId) {
        UserRecipe userRecipe = new UserRecipe(userId, recipeId);
        return userRecipeRepository.save(userRecipe);
    }

    public void delete(Long userId, Long recipeId) {
        userRecipeRepository.deleteByUserIdAndRecipeId(userId, recipeId);
    }

    public List<UserRecipe> getAllByUserId(Long userId) {
        return userRecipeRepository.findAll().stream()
                .filter(ur -> ur.getUserId().equals(userId))
                .toList();
    }

    public UserRecipe getByUserIdAndRecipeId(Long userId, Long recipeId) {
        return userRecipeRepository.findByUserIdAndRecipeId(userId, recipeId)
                .orElseThrow(() -> new RuntimeException("No se encontró la relación"));
    }
}
