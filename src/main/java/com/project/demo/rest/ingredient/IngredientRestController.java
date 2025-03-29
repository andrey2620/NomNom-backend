package com.project.demo.rest.ingredient;

import com.project.demo.logic.entity.ingredient.Ingredient;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.ingredient.IngredientRepository;
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
import java.util.Map;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ingredients")
public class IngredientRestController {

    @Autowired
    private IngredientService ingredientService;
    @Autowired
    private IngredientRepository ingredientRepository;

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

    // Buscar por nombre
    @GetMapping("/name/{ingredientName}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getIngredientByName(
            @PathVariable String ingredientName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        // Obtener todos los ingredientes que coincidan con el nombre (sin paginar en BD)
        List<Ingredient> matchingIngredients = ingredientRepository.findByNameContaining(ingredientName);

        System.out.println("Ingredientes encontrados: " + matchingIngredients.size());
        for (Ingredient ingredient : matchingIngredients) {
            System.out.println(ingredient.getName());
        }

        // Verificar si hay ingredientes encontrados
        if (matchingIngredients.isEmpty()) {
            return new GlobalResponseHandler().handleResponse(
                    "Ingredient name " + ingredientName + " not found",
                    null,
                    HttpStatus.NOT_FOUND,
                    request
            );
        }

        // Calcular los índices de paginación
        int start = (page - 1) * size;
        int end = Math.min(start + size, matchingIngredients.size());

        // Verificar si la página solicitada tiene resultados
        if (start >= matchingIngredients.size()) {
            return new GlobalResponseHandler().handleResponse(
                    "No more ingredients available for this page",
                    Collections.emptyList(),
                    HttpStatus.OK,
                    request
            );
        }

        // Obtener los ingredientes paginados
        List<Ingredient> paginatedIngredients = matchingIngredients.subList(start, end);

        // Crear el objeto Meta con información de paginación
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages((int) Math.ceil((double) matchingIngredients.size() / size));
        meta.setTotalElements(matchingIngredients.size());
        meta.setPageNumber(page);
        meta.setPageSize(size);

        return new GlobalResponseHandler().handleResponse(
                "Ingredients retrieved successfully",
                paginatedIngredients,
                HttpStatus.OK,
                meta
        );
    }




    // Crear un nuevo ingrediente
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