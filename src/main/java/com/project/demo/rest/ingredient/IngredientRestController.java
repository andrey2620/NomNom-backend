package com.project.demo.rest.ingredient;

import com.project.demo.logic.entity.ingredient.Ingredient;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.services.IngredientService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/ingredients")
public class IngredientRestController {

    @Autowired
    private IngredientService ingredientService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Ingredient> ingredientsPage = ingredientService.getAllIngredients(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(ingredientsPage.getTotalPages());
        meta.setTotalElements(ingredientsPage.getTotalElements());
        meta.setPageNumber(ingredientsPage.getNumber() + 1);
        meta.setPageSize(ingredientsPage.getSize());

        return new GlobalResponseHandler().handleResponse("Ingredients retrieved successfully",
                ingredientsPage.getContent(), HttpStatus.OK, meta);
    }

    @GetMapping("/{ingredientId}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getIngredientById(@PathVariable Long ingredientId, HttpServletRequest request) {
        Optional<Ingredient> foundIngredient = ingredientService.getIngredientById(ingredientId);

        if (foundIngredient.isPresent()) {
            return new GlobalResponseHandler().handleResponse(
                    "Ingredient retrieved successfully",
                    foundIngredient.get(),
                    HttpStatus.OK,
                    request
            );
        } else {
            return new GlobalResponseHandler().handleResponse(
                    "Ingredient id " + ingredientId + " not found",
                    null,
                    HttpStatus.NOT_FOUND,
                    request
            );
        }
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getIngredientsByUserId(@PathVariable Long userId, HttpServletRequest request) {
        List<Ingredient> userIngredients = ingredientService.getIngredientsByUserId(userId);
        return new GlobalResponseHandler().handleResponse(
                "Ingredients for user " + userId + " retrieved successfully",
                userIngredients,
                HttpStatus.OK,
                request
        );
    }

    @GetMapping("/formated/user/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getIngredientsByUserIdForIA(@PathVariable Long userId, HttpServletRequest request) {
        List<Ingredient> userIngredients = ingredientService.getIngredientsByUserId(userId);

        // Mapeamos solo a los nombres
        List<Map<String, String>> simplified = userIngredients.stream()
                .map(ingredient -> Map.of("name", ingredient.getName()))
                .toList();

        return new GlobalResponseHandler().handleResponse(
                "Ingredients for user " + userId + " retrieved successfully",
                simplified,
                HttpStatus.OK,
                request
        );
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> addIngredient(@RequestBody Ingredient ingredient, HttpServletRequest request) {
        Ingredient savedIngredient = ingredientService.saveIngredient(ingredient);
        return new GlobalResponseHandler().handleResponse("Ingredient created successfully",
                savedIngredient, HttpStatus.CREATED, request);
    }

    @PutMapping("/{ingredientId}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> updateIngredient(@PathVariable Long ingredientId, @RequestBody Ingredient ingredient, HttpServletRequest request) {
        Optional<Ingredient> foundIngredient = ingredientService.getIngredientById(ingredientId);

        if (foundIngredient.isPresent()) {
            Ingredient existingIngredient = foundIngredient.get();
            existingIngredient.setName(ingredient.getName());
            existingIngredient.setMedida(ingredient.getMedida());
            existingIngredient.setCreatedBy(ingredient.getCreatedBy());
            ingredientService.saveIngredient(existingIngredient);

            return new GlobalResponseHandler().handleResponse(
                    "Ingredient updated successfully",
                    existingIngredient,
                    HttpStatus.OK,
                    request
            );
        } else {
            return new GlobalResponseHandler().handleResponse(
                    "Ingredient id " + ingredientId + " not found",
                    null,
                    HttpStatus.NOT_FOUND,
                    request
            );
        }
    }

    @DeleteMapping("/{ingredientId}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> deleteIngredient(@PathVariable Long ingredientId, HttpServletRequest request) {
        if (!ingredientService.existsById(ingredientId)) {
            return new GlobalResponseHandler().handleResponse("Ingredient id " + ingredientId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
        ingredientService.deleteIngredient(ingredientId);
        return new GlobalResponseHandler().handleResponse("Ingredient deleted successfully",
                null, HttpStatus.OK, request);
    }
}