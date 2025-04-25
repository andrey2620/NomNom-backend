package com.project.demo.services;

import com.project.demo.logic.entity.recipe.Recipe;
import com.project.demo.logic.entity.recipe.RecipeRepository;
import com.project.demo.logic.entity.user_recipe.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class UserRecipeService {

    @Autowired
    private UserRecipeRepository userRecipeRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RecipeService recipeService;

    public UserRecipe save(Long userId, Long recipeId) {
        UserRecipe userRecipe = new UserRecipe(userId, recipeId);
        return userRecipeRepository.save(userRecipe);
    }

    @Transactional
    public void delete(Long userId, Long recipeId) {
        boolean exists = userRecipeRepository.findByUserIdAndRecipeId(userId, recipeId).isPresent();

        if (!exists) {
            throw new RuntimeException("La receta ya no está relacionada con el usuario.");
        }

        userRecipeRepository.deleteByUserIdAndRecipeId(userId, recipeId);

        boolean isOrphan = userRecipeRepository.findByRecipeId(recipeId).isEmpty();

        if (isOrphan) {
            recipeRepository.deleteById(recipeId);
        }
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
