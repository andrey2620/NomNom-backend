package com.project.demo.rest.user;

import com.project.demo.logic.entity.allergies.Allergies;
import com.project.demo.logic.entity.diet_preferences.Diet_Preferences;
import com.project.demo.logic.entity.allergies.AllergiesRepository;
import com.project.demo.logic.entity.diet_preferences.Diet_PreferenceRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.recipe.Recipe;
import com.project.demo.logic.entity.recipe.RecipeRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping("/users")
public class UserRestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AllergiesRepository allergiesRepository;

    @Autowired
    private Diet_PreferenceRepository diet_preferenceRepository;

    @Autowired
    private RecipeRepository recipeRepository;


    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    HttpServletRequest request) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<User> ordersPage = userRepository.findAll(pageable);
            Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
            meta.setTotalPages(ordersPage.getTotalPages());
            meta.setTotalElements(ordersPage.getTotalElements());
            meta.setPageNumber(ordersPage.getNumber() + 1);
            meta.setPageSize(ordersPage.getSize());

            return new GlobalResponseHandler().handleResponse("Users retrieved successfully",
                    ordersPage.getContent(), HttpStatus.OK, meta);
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error retrieving users: " + e.getMessage(),
                    null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> addUser(@RequestBody User user, HttpServletRequest request) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            return new GlobalResponseHandler().handleResponse("User updated successfully",
                    user, HttpStatus.OK, request);
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error adding user: " + e.getMessage(),
                    null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User user, HttpServletRequest request) {
        try {
            Optional<User> foundUser = userRepository.findById(userId);
            if (foundUser.isPresent()) {
                User userToUpdate = foundUser.get();

                userToUpdate.setName(user.getName());
                userToUpdate.setLastname(user.getLastname());
                userToUpdate.setEmail(user.getEmail());
                userToUpdate.setPicture(user.getPicture());

                // Actualizar ALERGIAS solo si llegaron datos
                if (user.getAllergies() != null && !user.getAllergies().isEmpty()) {
                    List<Allergies> existingAllergies = userToUpdate.getAllergies();
                    List<Long> selectedAllergiesIds = user.getAllergies().stream().map(Allergies::getId).toList();
                    existingAllergies.removeIf(allergy -> !selectedAllergiesIds.contains(allergy.getId()));
                    for (Allergies allergy : user.getAllergies()) {
                        if (existingAllergies.stream().noneMatch(a -> a.getId().equals(allergy.getId()))) {
                            allergiesRepository.findById(allergy.getId()).ifPresent(existingAllergies::add);
                        }
                    }
                    userToUpdate.setAllergies(existingAllergies);
                }

                // Actualizar PREFERENCIAS solo si llegaron datos
                if (user.getPreferences() != null && !user.getPreferences().isEmpty()) {
                    List<Diet_Preferences> existingPreferences = userToUpdate.getPreferences();
                    List<Long> selectedPreferencesIds = user.getPreferences().stream().map(Diet_Preferences::getId).toList();
                    existingPreferences.removeIf(pref -> !selectedPreferencesIds.contains(pref.getId()));
                    for (Diet_Preferences pref : user.getPreferences()) {
                        if (existingPreferences.stream().noneMatch(p -> p.getId().equals(pref.getId()))) {
                            diet_preferenceRepository.findById(pref.getId()).ifPresent(existingPreferences::add);
                        }
                    }
                    userToUpdate.setPreferences(existingPreferences);
                }

                userRepository.save(userToUpdate);
                return new GlobalResponseHandler().handleResponse(
                    "User updated successfully",
                    userToUpdate,
                    HttpStatus.OK,
                    request
                );
            } else {
                return new GlobalResponseHandler().handleResponse(
                    "User id " + userId + " not found",
                    HttpStatus.NOT_FOUND,
                    request
                );
            }
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error updating user: " + e.getMessage(),
                null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }


    @GetMapping("/allergies")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getAllAllergies(HttpServletRequest request) {
        try {
            List<Allergies> allergies = allergiesRepository.findAll();
            return new GlobalResponseHandler().handleResponse("Allergies retrieved successfully",
                    allergies, HttpStatus.OK, request);
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error retrieving allergies: " + e.getMessage(),
                    null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @GetMapping("/diet-preferences")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getAllDietPreferences(HttpServletRequest request) {
        try {
            List<Diet_Preferences> dietPreferences = diet_preferenceRepository.findAll();
            return new GlobalResponseHandler().handleResponse("Diet preferences retrieved successfully",
                    dietPreferences, HttpStatus.OK, request);
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error retrieving diet preferences: " + e.getMessage(),
                    null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId, HttpServletRequest request) {
        try {
            Optional<User> user = userRepository.findById(userId);
            if (user.isPresent()) {
                return new GlobalResponseHandler().handleResponse("User profile retrieved successfully",
                        user.get(), HttpStatus.OK, request);
            } else {
                return new GlobalResponseHandler().handleResponse("User not found",
                        HttpStatus.NOT_FOUND, request);
            }
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error retrieving user profile: " + e.getMessage(),
                    null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId, HttpServletRequest request) {
        try {
            Optional<User> foundOrder = userRepository.findById(userId);
            if (foundOrder.isPresent()) {
                userRepository.deleteById(userId);
                return new GlobalResponseHandler().handleResponse("User deleted successfully",
                        foundOrder.get(), HttpStatus.OK, request);
            } else {
                return new GlobalResponseHandler().handleResponse("Order id " + userId + " not found",
                        HttpStatus.NOT_FOUND, request);
            }
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error deleting user: " + e.getMessage(),
                    null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> authenticatedUser(HttpServletRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();

            user.getAllergies().size();
            user.getPreferences().size();

            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("name", user.getName());
            response.put("lastname", user.getLastname());
            response.put("email", user.getEmail());
            response.put("picture", user.getPicture());
            response.put("role", user.getRole());
            response.put("allergies", user.getAllergies());
            response.put("preferences", user.getPreferences());
            //response.put("menus", user.getMenus());
            //response.put("ingredients", user.getIngredients());

            return new GlobalResponseHandler().handleResponse(
                    "Authenticated user retrieved successfully",
                    response,
                    HttpStatus.OK,
                    request
            );
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error retrieving authenticated user: " + e.getMessage(),
                    null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }


    @PostMapping("/{userId}/favorites/{recipeId}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> addFavoriteRecipe(@PathVariable Long userId,
                                               @PathVariable Long recipeId,
                                               HttpServletRequest request) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            Optional<Recipe> recipeOpt = recipeRepository.findById(recipeId);

            if (userOpt.isEmpty()) {
                return new GlobalResponseHandler().handleResponse("Usuario no encontrado", HttpStatus.NOT_FOUND, request);
            }

            if (recipeOpt.isEmpty()) {
                return new GlobalResponseHandler().handleResponse("Receta no encontrada", HttpStatus.NOT_FOUND, request);
            }

            User user = userOpt.get();
            Recipe recipe = recipeOpt.get();

            user.getFavoriteRecipes().add(recipe);
            userRepository.save(user);

            return new GlobalResponseHandler().handleResponse("Receta guardada como favorita", recipe, HttpStatus.OK, request);
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error al guardar la receta: " + e.getMessage(),
                    null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @GetMapping("/me/favorites")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getFavoriteRecipes(HttpServletRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = (User) authentication.getPrincipal();

            List<Recipe> favoriteRecipes = new ArrayList<>(user.getFavoriteRecipes());

            return new GlobalResponseHandler().handleResponse(
                    "Recetas favoritas obtenidas correctamente",
                    favoriteRecipes,
                    HttpStatus.OK,
                    request
            );
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse("Error obteniendo recetas favoritas: " + e.getMessage(),
                    null, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }



}
