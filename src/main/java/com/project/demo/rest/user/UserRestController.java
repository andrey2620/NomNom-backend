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

        Pageable pageable = PageRequest.of(page-1, size);
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

//    @PutMapping("/{userId}")
//    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
//    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User user, HttpServletRequest request) {
//        Optional<User> foundOrder = userRepository.findById(userId);
//        if(foundOrder.isPresent()) {
//            user.setPassword(passwordEncoder.encode(user.getPassword()));
//            userRepository.save(user);
//            return new GlobalResponseHandler().handleResponse("User updated successfully",
//                    user, HttpStatus.OK, request);
//        } else {
//            return new GlobalResponseHandler().handleResponse("User id " + userId + " not found"  ,
//                    HttpStatus.NOT_FOUND, request);
//        }
//    }


// Actualizar un usuario existente (con alergias y preferencias)
@PutMapping("/{userId}")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User user, @RequestParam(required = false) Set<Long> selectedAllergies, @RequestParam(required = false) Set<Long> selectedDietPreferences,  HttpServletRequest request) {
    Optional<User> foundUser = userRepository.findById(userId);
    if (foundUser.isPresent()) {
        User existingUser = foundUser.get();
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());

        // Asignar alergias si se proporcionan
        if (selectedAllergies != null) {
            Set<Allergies> allergies = new HashSet<>(allergiesRepository.findAllById(selectedAllergies));
            List<Allergies> allergyList = new ArrayList<>(allergies);
            existingUser.setAllergies(allergyList);
        }

        // Asignar preferencias dietéticas si se proporcionan
        if (selectedDietPreferences != null) {
            Set<Diet_Preferences> dietPreferences = new HashSet<>(diet_preferenceRepository.findAllById(selectedDietPreferences));
            List<Diet_Preferences> dietPreferenceList = new ArrayList<>(dietPreferences);
            existingUser.setPreferences(dietPreferenceList);
        }

        // Guardar el usuario actualizado
        userRepository.save(existingUser);
        return new GlobalResponseHandler().handleResponse("User updated successfully",
            existingUser, HttpStatus.OK, request);
    } else {
        return new GlobalResponseHandler().handleResponse("User id " + userId + " not found",
            HttpStatus.NOT_FOUND, request);
    }
}

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId, HttpServletRequest request) {
        Optional<User> foundOrder = userRepository.findById(userId);
        if(foundOrder.isPresent()) {
            userRepository.deleteById(userId);
            return new GlobalResponseHandler().handleResponse("User deleted successfully",
                    foundOrder.get(), HttpStatus.OK, request);
        } else {
            return new GlobalResponseHandler().handleResponse("Order id " + userId + " not found"  ,
                    HttpStatus.NOT_FOUND, request);
        }
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public User authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

}