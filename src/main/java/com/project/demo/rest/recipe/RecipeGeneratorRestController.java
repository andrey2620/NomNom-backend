package com.project.demo.rest.recipe;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.demo.services.RecipeGeneratorService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recipes/generator")
@PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
public class RecipeGeneratorRestController {

    private final RecipeGeneratorService recipeGeneratorService;

    public RecipeGeneratorRestController(RecipeGeneratorService recipeGeneratorService) {
        this.recipeGeneratorService = recipeGeneratorService;
    }

    // Generador de recetas con todos los ingredientes (archivo JSON)
    @GetMapping
    public JsonNode generateRecipe() throws Exception {
        return recipeGeneratorService.generateRecipeWithAllIngredients();
    }

    // Generador de recetas usando solo ingredientes del usuario
    @GetMapping("/user/{userId}")
    public JsonNode generateUserRecipe(@PathVariable Long userId) throws Exception {
        return recipeGeneratorService.generateRecipeForUser(userId);
    }
}
