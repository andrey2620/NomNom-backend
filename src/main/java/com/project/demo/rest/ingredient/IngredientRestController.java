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
import java.util.stream.Collectors;

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
        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<Ingredient> ingredientsPage = ingredientService.getAllIngredients(pageable);
            Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
            meta.setTotalPages(ingredientsPage.getTotalPages());
            meta.setTotalElements(ingredientsPage.getTotalElements());
            meta.setPageNumber(ingredientsPage.getNumber() + 1);
            meta.setPageSize(ingredientsPage.getSize());

            return new GlobalResponseHandler().handleResponse("Ingredients retrieved successfully",
                    ingredientsPage.getContent(), HttpStatus.OK, meta);
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error retrieving ingredients: " + e.getMessage(),
                    null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @GetMapping("/{ingredientId}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getIngredientById(@PathVariable Long ingredientId, HttpServletRequest request) {
        try {
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
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error retrieving ingredient: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
            );
        }
    }

    @GetMapping("/formated/user/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getIngredientsByUserId(@PathVariable Long userId, HttpServletRequest request) {
        try {
            List<String> ingredients = ingredientService.getFormattedIngredientsByUserId(userId);

            return new GlobalResponseHandler().handleResponse(
                    "Ingredientes del usuario recuperados correctamente",
                    ingredients,
                    HttpStatus.OK,
                    request
            );
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error al obtener ingredientes: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
            );
        }
    }


    @GetMapping("/name/{ingredientName}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getIngredientByName(
            @PathVariable String ingredientName,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        try {
            List<Ingredient> matchingIngredients = ingredientRepository.findByNameContaining(ingredientName);

            if (matchingIngredients.isEmpty()) {
                return new GlobalResponseHandler().handleResponse(
                        "Ingredient name " + ingredientName + " not found",
                        null,
                        HttpStatus.NOT_FOUND,
                        request
                );
            }

            int start = (page - 1) * size;
            int end = Math.min(start + size, matchingIngredients.size());

            if (start >= matchingIngredients.size()) {
                return new GlobalResponseHandler().handleResponse(
                        "No more ingredients available for this page",
                        Collections.emptyList(),
                        HttpStatus.OK,
                        request
                );
            }

            List<Ingredient> paginatedIngredients = matchingIngredients.subList(start, end);

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
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error retrieving ingredients by name: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
            );
        }
    }

    @GetMapping("/category/{ingredientCategory}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getIngredientByCategory(
            @PathVariable String ingredientCategory,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        try {
            List<Ingredient> matchingIngredients = ingredientRepository.findByCategoryContaining(ingredientCategory);

            if (matchingIngredients.isEmpty()) {
                return new GlobalResponseHandler().handleResponse(
                        "Ingredient category " + ingredientCategory + " not found",
                        null,
                        HttpStatus.NOT_FOUND,
                        request
                );
            }

            int start = (page - 1) * size;
            int end = Math.min(start + size, matchingIngredients.size());

            if (start >= matchingIngredients.size()) {
                return new GlobalResponseHandler().handleResponse(
                        "No more ingredients available for this page",
                        Collections.emptyList(),
                        HttpStatus.OK,
                        request
                );
            }

            List<Ingredient> paginatedIngredients = matchingIngredients.subList(start, end);

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
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error retrieving ingredients by category: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
            );
        }
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getIngredientsByCategoryAndName(
            @RequestParam String category,  // Filtro por categoría obligatorio
            @RequestParam String name,     // Filtro por nombre obligatorio
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        try {

            // Primero, obtenemos los ingredientes que coinciden con la categoría
            List<Ingredient> matchingIngredients = ingredientRepository.findByCategoryContaining(category);

            if (matchingIngredients.isEmpty()) {
                return new GlobalResponseHandler().handleResponse(
                        "Ingredient category " + category + " not found",
                        null,
                        HttpStatus.NOT_FOUND,
                        request
                );
            }

            // Luego, filtramos los ingredientes obtenidos por nombre
            matchingIngredients = matchingIngredients.stream()
                    .filter(ingredient -> ingredient.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());

            /*if (matchingIngredients.isEmpty()) {
                return new GlobalResponseHandler().handleResponse(
                        "No ingredients found for the category " + category + " with name containing " + name,
                        null,
                        HttpStatus.NOT_FOUND,
                        request
                );
            }*/

            // Paginación
            int start = (page - 1) * size;
            int end = Math.min(start + size, matchingIngredients.size());

            if (start >= matchingIngredients.size()) {
                return new GlobalResponseHandler().handleResponse(
                        "No more ingredients available for this page",
                        Collections.emptyList(),
                        HttpStatus.OK,
                        request
                );
            }

            List<Ingredient> paginatedIngredients = matchingIngredients.subList(start, end);

            // Meta información
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
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error retrieving ingredients: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
            );
        }
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> addIngredient(@RequestBody Ingredient ingredient, HttpServletRequest request) {
        try {
            Ingredient savedIngredient = ingredientService.saveIngredient(ingredient);
            return new GlobalResponseHandler().handleResponse("Ingredient created successfully",
                    savedIngredient, HttpStatus.CREATED, request);
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error creating ingredient: " + e.getMessage(),
                    null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @PostMapping("/create/with-user/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<Ingredient> createIngredientForUser(@RequestBody Ingredient ingredient, @PathVariable Long userId) {
        try {
            Ingredient saved = ingredientService.createIngredientAndAssignToUser(ingredient, userId);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/link/{ingredientId}/user/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> linkIngredientToUser(
            @PathVariable Long ingredientId,
            @PathVariable Long userId,
            HttpServletRequest request) {

        try {
            String resultMessage = ingredientService.linkExistingIngredientToUser(ingredientId, userId);
            return new GlobalResponseHandler().handleResponse(
                    resultMessage,
                    null,
                    HttpStatus.OK,
                    request
            );
        } catch (IllegalArgumentException e) {
            return new GlobalResponseHandler().handleResponse(
                    e.getMessage(),
                    null,
                    HttpStatus.NOT_FOUND,
                    request
            );
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error al vincular ingrediente: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
            );
        }
    }

    @PostMapping("/bulk-link/user/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> bulkLinkIngredientsToUser(
            @PathVariable Long userId,
            @RequestBody List<Long> ingredientIds,
            HttpServletRequest request) {
        try {
            Map<Long, String> result = ingredientService.bulkLinkIngredientsToUser(ingredientIds, userId);
            return new GlobalResponseHandler().handleResponse(
                    "Resultado del vínculo múltiple de ingredientes.",
                    result,
                    HttpStatus.OK,
                    request
            );
        } catch (IllegalArgumentException e) {
            return new GlobalResponseHandler().handleResponse(
                    e.getMessage(),
                    null,
                    HttpStatus.NOT_FOUND,
                    request
            );
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error al realizar vínculo múltiple: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
            );
        }
    }

    @PutMapping("/{ingredientId}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> updateIngredient(@PathVariable Long ingredientId, @RequestBody Ingredient ingredient, HttpServletRequest request) {
        try {
            Optional<Ingredient> foundIngredient = ingredientService.getIngredientById(ingredientId);

            if (foundIngredient.isPresent()) {
                Ingredient existingIngredient = foundIngredient.get();
                existingIngredient.setName(ingredient.getName());
                existingIngredient.setMedida(ingredient.getMedida());
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
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error updating ingredient: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
            );
        }
    }

    @DeleteMapping("/{ingredientId}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> deleteIngredient(@PathVariable Long ingredientId, HttpServletRequest request) {
        try {
            if (!ingredientService.existsById(ingredientId)) {
                return new GlobalResponseHandler().handleResponse("Ingredient id " + ingredientId + " not found",
                        HttpStatus.NOT_FOUND, request);
            }
            ingredientService.deleteIngredient(ingredientId);
            return new GlobalResponseHandler().handleResponse("Ingredient deleted successfully",
                    null, HttpStatus.OK, request);
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "Error deleting ingredient: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
            );
        }
    }
}
