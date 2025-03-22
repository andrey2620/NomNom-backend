package com.project.demo.rest.recipe;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.demo.services.RecipeGeneratorService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recipes/generator")
public class RecipeGeneratorRestController {

    private final RecipeGeneratorService recipeGeneratorService;

    public RecipeGeneratorRestController(RecipeGeneratorService recipeGeneratorService) {
        this.recipeGeneratorService = recipeGeneratorService;
    }

    @GetMapping
    public JsonNode generateRecipe() throws Exception {
        return recipeGeneratorService.generateSingleRecipe();
    }
}
