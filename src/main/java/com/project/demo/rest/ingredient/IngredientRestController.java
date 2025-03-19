package com.project.demo.rest.ingredient;

import com.project.demo.logic.entity.ingredient.Ingredient;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.ingredient.IngredientRepository;
import com.project.demo.logic.entity.user.UserRepository;
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
@RequestMapping("/ingredients")
public class IngredientRestController {

    @Autowired
    private IngredientRepository ingredientRepository;

    // Obtener todos los ingredientes (paginado)
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Ingredient> ingredientsPage = ingredientRepository.findAll(pageable);
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
        Optional<Ingredient> foundIngredient = ingredientRepository.findById(ingredientId);

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
                    null,  // Se pasa `null` si no hay contenido
                    HttpStatus.NOT_FOUND,
                    request
            );
        }
    }



    // Crear un nuevo ingrediente
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> addIngredient(@RequestBody Ingredient ingredient, HttpServletRequest request) {
        Ingredient savedIngredient = ingredientRepository.save(ingredient);
        return new GlobalResponseHandler().handleResponse("Ingredient created successfully",
                savedIngredient, HttpStatus.CREATED, request);
    }

    @PutMapping("/{ingredientId}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> updateIngredient(@PathVariable Long ingredientId, @RequestBody Ingredient ingredient, HttpServletRequest request) {
        Optional<Ingredient> foundIngredient = ingredientRepository.findById(ingredientId);

        if (foundIngredient.isPresent()) {
            Ingredient existingIngredient = foundIngredient.get();
            existingIngredient.setName(ingredient.getName());
            existingIngredient.setUserId(ingredient.getUserId());
            ingredientRepository.save(existingIngredient);

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


    // Eliminar un ingrediente
    @DeleteMapping("/{ingredientId}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> deleteIngredient(@PathVariable Long ingredientId, HttpServletRequest request) {
        if (!ingredientRepository.existsById(ingredientId)) {
            return new GlobalResponseHandler().handleResponse("Ingredient id " + ingredientId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
        ingredientRepository.deleteById(ingredientId);
        return new GlobalResponseHandler().handleResponse("Ingredient deleted successfully",
                null, HttpStatus.OK, request);
    }
}