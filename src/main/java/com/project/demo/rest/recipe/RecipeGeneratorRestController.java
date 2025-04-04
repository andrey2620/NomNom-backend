package com.project.demo.rest.recipe;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.services.RecipeGeneratorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @PostMapping("/custom")
    public JsonNode generateCustomRecipe(@RequestBody List<String> ingredients) throws Exception {
        return recipeGeneratorService.generateRecipeFromIngredients(ingredients);
    }

    @PostMapping("/suggestions")
    public ResponseEntity<?> getRecipeSuggestions(@RequestBody Map<String, Object> recipeJson, HttpServletRequest request) {
        try {
            JsonNode suggestions = recipeGeneratorService.getSuggestionsForRecipe(recipeJson);
            return new GlobalResponseHandler().handleResponse(
                    "Sugerencias generadas exitosamente",
                    suggestions,
                    HttpStatus.OK,
                    request
            );
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error al generar sugerencias para la receta",
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
            );
        }
    }
}
