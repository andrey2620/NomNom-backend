package com.project.demo.rest.user;

import com.project.demo.logic.entity.allergies.Allergies;
import com.project.demo.logic.entity.diet_preferences.Diet_Preferences;
import com.project.demo.logic.entity.allergies.AllergiesRepository;
import com.project.demo.logic.entity.diet_preferences.Diet_PreferenceRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


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

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> ordersPage = userRepository.findAll(pageable);
        Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
        meta.setTotalPages(ordersPage.getTotalPages());
        meta.setTotalElements(ordersPage.getTotalElements());
        meta.setPageNumber(ordersPage.getNumber() + 1);
        meta.setPageSize(ordersPage.getSize());

        return new GlobalResponseHandler().handleResponse("Users retrieved successfully",
                ordersPage.getContent(), HttpStatus.OK, meta);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> addUser(@RequestBody User user, HttpServletRequest request) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return new GlobalResponseHandler().handleResponse("User updated successfully",
                user, HttpStatus.OK, request);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User user, HttpServletRequest request) {
        Optional<User> foundUser = userRepository.findById(userId);
        if (foundUser.isPresent()) {
            User userToUpdate = foundUser.get();

            userToUpdate.setName(user.getName());
            userToUpdate.setLastname(user.getLastname());
            userToUpdate.setEmail(user.getEmail());
            userToUpdate.setPicture(user.getPicture());

            // ✅ Alergias existentes
            List<Allergies> existingAllergies = userToUpdate.getAllergies();

            // ✅ Alergias seleccionadas desde el frontend
            List<Long> selectedAllergiesIds = user.getAllergies().stream()
                .map(Allergies::getId)
                .toList();

            // ✅ Mantener solo las que están seleccionadas
            existingAllergies.removeIf(allergy -> !selectedAllergiesIds.contains(allergy.getId()));

            // ✅ Agregar nuevas alergias que no estén
            for (Allergies allergy : user.getAllergies()) {
                if (existingAllergies.stream().noneMatch(a -> a.getId().equals(allergy.getId()))) {
                    allergiesRepository.findById(allergy.getId()).ifPresent(existingAllergies::add);
                }
            }

            userToUpdate.setAllergies(existingAllergies);

            // 🔥 Hacemos lo mismo para preferencias dietéticas
            List<Diet_Preferences> existingPreferences = userToUpdate.getPreferences();

            List<Long> selectedPreferencesIds = user.getPreferences().stream()
                .map(Diet_Preferences::getId)
                .toList();

            existingPreferences.removeIf(pref -> !selectedPreferencesIds.contains(pref.getId()));

            for (Diet_Preferences pref : user.getPreferences()) {
                if (existingPreferences.stream().noneMatch(p -> p.getId().equals(pref.getId()))) {
                    diet_preferenceRepository.findById(pref.getId()).ifPresent(existingPreferences::add);
                }
            }

            userToUpdate.setPreferences(existingPreferences);

            // ✅ Finalmente guardamos
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
    }


    @GetMapping("/allergies")
    public ResponseEntity<?> getAllAllergies(HttpServletRequest request) {
        List<Allergies> allergies = allergiesRepository.findAll();
        return new GlobalResponseHandler().handleResponse("Allergies retrieved successfully",
            allergies, HttpStatus.OK, request);
    }

    @GetMapping("/diet-preferences")
    public ResponseEntity<?> getAllDietPreferences(HttpServletRequest request) {
        List<Diet_Preferences> dietPreferences = diet_preferenceRepository.findAll();
        return new GlobalResponseHandler().handleResponse("Diet preferences retrieved successfully",
            dietPreferences, HttpStatus.OK, request);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long userId, HttpServletRequest request) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return new GlobalResponseHandler().handleResponse("User profile retrieved successfully",
                user.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("User not found",
                HttpStatus.NOT_FOUND, request);
        }
    }


    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId, HttpServletRequest request) {
        Optional<User> foundOrder = userRepository.findById(userId);
        if (foundOrder.isPresent()) {
            userRepository.deleteById(userId);
            return new GlobalResponseHandler().handleResponse("User deleted successfully",
                    foundOrder.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Order id " + userId + " not found",
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> authenticatedUser(HttpServletRequest request) {
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

        return new GlobalResponseHandler().handleResponse(
            "Authenticated user retrieved successfully",
            response,
            HttpStatus.OK,
            request
        );
    }
}