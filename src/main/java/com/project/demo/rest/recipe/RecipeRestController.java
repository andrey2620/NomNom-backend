package com.project.demo.rest.recipe;

import com.project.demo.logic.entity.recipe.Recipe;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.services.RecipeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/recipes")
public class RecipeRestController {

    @Autowired
    private RecipeService recipeService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Recipe> recipePage = recipeService.getAllRecipes(pageable);

        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(recipePage.getTotalPages());
        meta.setTotalElements(recipePage.getTotalElements());
        meta.setPageNumber(recipePage.getNumber() + 1);
        meta.setPageSize(recipePage.getSize());

        return new GlobalResponseHandler().handleResponse("Recipes retrieved successfully",
                recipePage.getContent(), HttpStatus.OK, meta);
    }

    @GetMapping("/{recipeId}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getRecipeById(@PathVariable Long recipeId, HttpServletRequest request) {
        Optional<Recipe> recipe = recipeService.getRecipeById(recipeId);

        if (recipe.isPresent()) {
            return new GlobalResponseHandler().handleResponse(
                    "Recipe retrieved successfully",
                    recipe.get(),
                    HttpStatus.OK,
                    request
            );
        } else {
            return new GlobalResponseHandler().handleResponse(
                    "Recipe id " + recipeId + " not found",
                    null,
                    HttpStatus.NOT_FOUND,
                    request
            );
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> addRecipe(@RequestBody Recipe recipe, HttpServletRequest request) {
        Recipe saved = recipeService.saveRecipe(recipe);
        return new GlobalResponseHandler().handleResponse(
                "Recipe created successfully",
                saved,
                HttpStatus.CREATED,
                request
        );
    }

    @PutMapping("/{recipeId}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> updateRecipe(@PathVariable Long recipeId, @RequestBody Recipe recipe, HttpServletRequest request) {
        Optional<Recipe> existing = recipeService.getRecipeById(recipeId);
        if (existing.isPresent()) {
            Recipe toUpdate = existing.get();
            toUpdate.setName(recipe.getName());
            toUpdate.setDescription(recipe.getDescription());
            toUpdate.setInstructions(recipe.getInstructions());
            toUpdate.setImageUrl(recipe.getImageUrl());
            toUpdate.setPreparationTime(recipe.getPreparationTime());
            toUpdate.setRecipeCategory(recipe.getRecipeCategory());
            toUpdate.setNutritionalInfo(recipe.getNutritionalInfo());

            Recipe updated = recipeService.saveRecipe(toUpdate);

            return new GlobalResponseHandler().handleResponse(
                    "Recipe updated successfully",
                    updated,
                    HttpStatus.OK,
                    request
            );
        } else {
            return new GlobalResponseHandler().handleResponse(
                    "Recipe id " + recipeId + " not found",
                    null,
                    HttpStatus.NOT_FOUND,
                    request
            );
        }
    }

    @DeleteMapping("/{recipeId}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> deleteRecipe(@PathVariable Long recipeId, HttpServletRequest request) {
        if (!recipeService.existsById(recipeId)) {
            return new GlobalResponseHandler().handleResponse(
                    "Recipe id " + recipeId + " not found",
                    HttpStatus.NOT_FOUND,
                    request
            );
        }
        recipeService.deleteRecipe(recipeId);
        return new GlobalResponseHandler().handleResponse(
                "Recipe deleted successfully",
                null,
                HttpStatus.OK,
                request
        );
    }
}