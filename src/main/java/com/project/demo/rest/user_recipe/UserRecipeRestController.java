package com.project.demo.rest.user_recipe;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.recipe.Recipe;
import com.project.demo.logic.entity.user_recipe.UserRecipe;
import com.project.demo.services.UserRecipeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-recipes")
public class UserRecipeRestController {

    @Autowired
    private UserRecipeService userRecipeService;

    @PostMapping
    public ResponseEntity<?> saveUserRecipe(@RequestParam Long userId,
                                            @RequestParam Long recipeId,
                                            HttpServletRequest request) {
        try {
            UserRecipe saved = userRecipeService.save(userId, recipeId);
            return new GlobalResponseHandler().handleResponse(
                    "Receta guardada correctamente para el usuario.",
                    saved,
                    HttpStatus.OK,
                    request
            );
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error al guardar receta: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
            );
        }
    }

    @DeleteMapping
    public ResponseEntity<?> deleteUserRecipe(@RequestParam Long userId,
                                              @RequestParam Long recipeId,
                                              HttpServletRequest request) {
        try {
            userRecipeService.delete(userId, recipeId);
            return new GlobalResponseHandler().handleResponse(
                    "Relación receta-usuario eliminada correctamente.",
                    null,
                    HttpStatus.OK,
                    request
            );
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error al eliminar receta del usuario: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
            );
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllByUserId(@RequestParam Long userId,
                                            HttpServletRequest request) {
        try {
            List<Recipe> recipes = userRecipeService.getAllRecipesByUserId(userId);
            return new GlobalResponseHandler().handleResponse(
                    "Recetas completas del usuario obtenidas correctamente.",
                    recipes,
                    HttpStatus.OK,
                    request
            );
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error al obtener recetas del usuario: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
            );
        }
    }


    @GetMapping
    public ResponseEntity<?> getByUserIdAndRecipeId(@RequestParam Long userId,
                                                    @RequestParam Long recipeId,
                                                    HttpServletRequest request) {
        try {
            UserRecipe result = userRecipeService.getByUserIdAndRecipeId(userId, recipeId);
            return new GlobalResponseHandler().handleResponse(
                    "Relación usuario-receta encontrada.",
                    result,
                    HttpStatus.OK,
                    request
            );
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error al obtener relación: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
            );
        }
    }
}
