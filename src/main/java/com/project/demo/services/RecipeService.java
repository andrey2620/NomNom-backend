package com.project.demo.services;

import com.project.demo.logic.entity.ingredient.Ingredient;
import com.project.demo.logic.entity.recipe.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private IngredientService ingredientService;

    public Recipe saveRecipeFromIA(RecipeFromIARequest dto) {
        Recipe recipe = new Recipe();
        recipe.setName(dto.getName());
        recipe.setRecipeCategory(RecipeCategory.valueOf(dto.getRecipeCategory()));
        recipe.setPreparationTime(dto.getPreparationTime());
        recipe.setDescription(dto.getDescription());
        recipe.setNutritionalInfo(dto.getNutritionalInfo());
        recipe.setInstructions(dto.getInstructions());

        for (RecipeFromIARequest.IngredientDTO ing : dto.getIngredients()) {
            Ingredient ingredient = ingredientService.findOrCreateByName(ing.name, ing.measurement);
            RecipeIngredient ri = new RecipeIngredient(recipe, ingredient, ing.quantity, ing.measurement);
            recipe.getRecipeIngredients().add(ri);
        }

        return recipeRepository.save(recipe);
    }

    public Page<Recipe> getAllRecipes(Pageable pageable) {
        return recipeRepository.findAll(pageable);
    }

    public Optional<Recipe> getRecipeById(Long id) {
        return recipeRepository.findById(id);
    }

    public Recipe saveRecipe(Recipe recipe) {
        return recipeRepository.save(recipe);
    }

    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return recipeRepository.existsById(id);
    }
}