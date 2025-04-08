package com.project.demo.rest.allergies;

import com.project.demo.logic.entity.allergies.Allergies;
import com.project.demo.logic.entity.allergies.AllergiesRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/allergies")
public class AllergiesRestController {
    @Autowired
    private AllergiesRepository allergiesRepository;
    private User userDetails;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<Allergies> allergiesPage = allergiesRepository.findAll(pageable);
            Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
            meta.setTotalPages(allergiesPage.getTotalPages());
            meta.setTotalElements(allergiesPage.getTotalElements());
            meta.setPageNumber(allergiesPage.getNumber() + 1);
            meta.setPageSize(allergiesPage.getSize());

            Optional<User> optionalUser = userRepository.findByName(userDetails.getUsername());
            List<Allergies> userAllergies = optionalUser.map(User::getAllergies).orElse(Collections.emptyList());

            List<Map<String, Object>> result = new ArrayList<>();
            for (Allergies allergy : allergiesPage.getContent()) {
                Map<String, Object> allergyMap = new HashMap<>();
                allergyMap.put("id", allergy.getId());
                allergyMap.put("name", allergy.getName());
                allergyMap.put("isSelected", userAllergies.stream()
                        .anyMatch(userAllergy -> userAllergy.getId().equals(allergy.getId())));
                result.add(allergyMap);
            }

            return new GlobalResponseHandler().handleResponse(
                    "Allergies retrieved successfully",
                    result,
                    HttpStatus.OK,
                    meta
            );
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "An error occurred while retrieving allergies: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
            );
        }
    }

    @GetMapping("/{allergyId}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getById(@PathVariable Long allergyId, HttpServletRequest request) {
        try {
            Optional<Allergies> foundAllergy = allergiesRepository.findById(allergyId);

            if (foundAllergy.isPresent()) {
                return new GlobalResponseHandler().handleResponse(
                        "Allergy retrieved successfully",
                        foundAllergy.get(),
                        HttpStatus.OK,
                        request
                );
            } else {
                return new GlobalResponseHandler().handleResponse(
                        "Allergy id " + allergyId + " not found",
                        null,
                        HttpStatus.NOT_FOUND,
                        request
                );
            }
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "An error occurred while retrieving the allergy: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
            );
        }
    }

    @GetMapping("/name/{allergyName}")
    @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
    public ResponseEntity<?> getByName(@PathVariable String allergyName, HttpServletRequest request) {
        try {
            Optional<Allergies> foundAllergy = allergiesRepository.findByName(allergyName);

            if (foundAllergy.isPresent()) {
                return new GlobalResponseHandler().handleResponse(
                        "Allergy retrieved successfully",
                        foundAllergy.get(),
                        HttpStatus.OK,
                        request
                );
            } else {
                return new GlobalResponseHandler().handleResponse(
                        "Allergy with name '" + allergyName + "' not found",
                        null,
                        HttpStatus.NOT_FOUND,
                        request
                );
            }
        } catch (Exception e) {
            return new GlobalResponseHandler().handleResponse(
                    "An error occurred while retrieving the allergy: " + e.getMessage(),
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request
            );
        }
    }
}
