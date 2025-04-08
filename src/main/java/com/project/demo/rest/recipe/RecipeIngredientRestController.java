package com.project.demo.rest.recipe;

import com.project.demo.logic.entity.recipe.RecipeIngredient;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.recipe.RecipeIngredientDTO;
import com.project.demo.services.RecipeIngredientService;
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
import java.util.Optional;

@RestController
@RequestMapping("/recipe/ingredient")
public class RecipeIngredientRestController {

    @Autowired
    private RecipeIngredientService recipeIngredientService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<RecipeIngredient> pageResult = recipeIngredientService.getAll(pageable);

            List<RecipeIngredientDTO> dtos = pageResult.getContent()
                    .stream()
                    .map(ri -> new RecipeIngredientDTO(
                            ri.getId(),
                            ri.getIngredient().getId(),
                            ri.getRecipe().getId(),
                            ri.getQuantity(),
                            ri.getMeasurement()
                    ))
                    .toList();

            Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
            meta.setTotalPages(pageResult.getTotalPages());
            meta.setTotalElements(pageResult.getTotalElements());
            meta.setPageNumber(pageResult.getNumber() + 1);
            meta.setPageSize(pageResult.getSize());

            return new GlobalResponseHandler().handleResponse(
                    "RecipeIngredients retrieved successfully",
                    dtos,
                    HttpStatus.OK,
                    meta
            );
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error retrieving recipe ingredients: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
            );
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getById(@PathVariable Long id, HttpServletRequest request) {
        try {
            Optional<RecipeIngredient> item = recipeIngredientService.getById(id);
            if (item.isPresent()) {
                return new GlobalResponseHandler().handleResponse(
                        "RecipeIngredient retrieved successfully",
                        item.get(),
                        HttpStatus.OK,
                        request
                );
            } else {
                return new GlobalResponseHandler().handleResponse(
                        "RecipeIngredient id " + id + " not found",
                        null,
                        HttpStatus.NOT_FOUND,
                        request
                );
            }
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error retrieving recipe ingredient: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
            );
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> create(@RequestBody RecipeIngredient entity, HttpServletRequest request) {
        try {
            RecipeIngredient saved = recipeIngredientService.save(entity);
            return new GlobalResponseHandler().handleResponse(
                    "RecipeIngredient created successfully",
                    saved,
                    HttpStatus.CREATED,
                    request
            );
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error creating recipe ingredient: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
            );
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody RecipeIngredient updatedData, HttpServletRequest request) {
        try {
            Optional<RecipeIngredient> existing = recipeIngredientService.getById(id);
            if (existing.isPresent()) {
                RecipeIngredient item = existing.get();
                item.setIngredient(updatedData.getIngredient());
                item.setRecipe(updatedData.getRecipe());
                item.setQuantity(updatedData.getQuantity());
                item.setMeasurement(updatedData.getMeasurement());

                RecipeIngredient updated = recipeIngredientService.save(item);

                return new GlobalResponseHandler().handleResponse(
                        "RecipeIngredient updated successfully",
                        updated,
                        HttpStatus.OK,
                        request
                );
            } else {
                return new GlobalResponseHandler().handleResponse(
                        "RecipeIngredient id " + id + " not found",
                        null,
                        HttpStatus.NOT_FOUND,
                        request
                );
            }
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error updating recipe ingredient: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
            );
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpServletRequest request) {
        try {
            if (!recipeIngredientService.existsById(id)) {
                return new GlobalResponseHandler().handleResponse(
                        "RecipeIngredient id " + id + " not found",
                        HttpStatus.NOT_FOUND,
                        request
                );
            }
            recipeIngredientService.delete(id);
            return new GlobalResponseHandler().handleResponse(
                    "RecipeIngredient deleted successfully",
                    null,
                    HttpStatus.OK,
                    request
            );
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error deleting recipe ingredient: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
            );
        }
    }
}
