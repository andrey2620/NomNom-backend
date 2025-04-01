package com.project.demo.rest.recipe;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.demo.services.RecipeGeneratorService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/recipes/generator")
@PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
public class RecipeGeneratorRestController {

    private final RecipeGeneratorService recipeGeneratorService;

    public RecipeGeneratorRestController(RecipeGeneratorService recipeGeneratorService) {
        this.recipeGeneratorService = recipeGeneratorService;
    }

    @GetMapping
    public JsonNode generateRecipe() throws Exception {
        return recipeGeneratorService.generateRecipeWithAllIngredients();
    }

    @GetMapping("/user/{userId}")
    public JsonNode generateUserRecipe(@PathVariable Long userId) throws Exception {
        return recipeGeneratorService.generateRecipeForUser(userId);
    }

    @PostMapping("/suggestions")
    public ResponseEntity<?> getRecipeSuggestions(@RequestBody Map<String, Object> recipeJson) {
        try {
            JsonNode suggestions = recipeGeneratorService.getSuggestionsForRecipe(recipeJson);
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar sugerencias para la receta", e);
        }
    }
}
