package com.project.demo.rest.recipe;

import com.project.demo.services.RecipeGeneratorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recipes/generator")
public class RecipeGeneratorRestController {

    private final RecipeGeneratorService recipeGeneratorService;

    public RecipeGeneratorRestController(RecipeGeneratorService recipeGeneratorService) {
        this.recipeGeneratorService = recipeGeneratorService;
    }

    @GetMapping
    public List<String> generateRecipes() throws Exception {
        return recipeGeneratorService.generateRecipesFromJsonIngredients();
    }
}
