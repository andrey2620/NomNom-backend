package com.project.demo.services;

import com.project.demo.logic.entity.recipe.Recipe;
import com.project.demo.logic.entity.recipe.RecipeRepository;
import com.project.demo.logic.entity.user_recipe.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserRecipeService {

    @Autowired
    private UserRecipeRepository userRecipeRepository;

    @Autowired
    private RecipeRepository recipeRepository;


    public UserRecipe save(Long userId, Long recipeId) {
        UserRecipe userRecipe = new UserRecipe(userId, recipeId);
        return userRecipeRepository.save(userRecipe);
    }

    public void delete(Long userId, Long recipeId) {
        userRecipeRepository.deleteByUserIdAndRecipeId(userId, recipeId);
    }

    public List<Recipe> getAllRecipesByUserId(Long userId) {
        List<Long> recipeIds = userRecipeRepository.findByUserId(userId)
                .stream()
                .map(UserRecipe::getRecipeId)
                .distinct()
                .toList();

        return recipeRepository.findAllById(recipeIds);
    }


    public UserRecipe getByUserIdAndRecipeId(Long userId, Long recipeId) {
        return userRecipeRepository.findByUserIdAndRecipeId(userId, recipeId)
                .orElseThrow(() -> new RuntimeException("No se encontró la relación"));
    }
}
