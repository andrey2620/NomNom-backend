package com.project.demo.rest.diet_preferences;

import com.project.demo.logic.entity.diet_preferences.Diet_Preferences;
import com.project.demo.logic.entity.diet_preferences.Diet_PreferenceRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.http.Meta;
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
@RequestMapping("/diet_preferences")
public class DietPreferencesRestController {

  @Autowired
  private Diet_PreferenceRepository dietPreferenceRepository;

  // Obtener todas las preferencias de dieta (paginado)
  @GetMapping
  @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
  public ResponseEntity<?> getAll(
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size,
      HttpServletRequest request) {

    Pageable pageable = PageRequest.of(page - 1, size);
    Page<Diet_Preferences> dietPreferencesPage = dietPreferenceRepository.findAll(pageable);
    Meta meta = new Meta(request.getMethod(), request.getRequestURL().toString());
    meta.setTotalPages(dietPreferencesPage.getTotalPages());
    meta.setTotalElements(dietPreferencesPage.getTotalElements());
    meta.setPageNumber(dietPreferencesPage.getNumber() + 1);
    meta.setPageSize(dietPreferencesPage.getSize());

    return new GlobalResponseHandler().handleResponse(
        "Diet preferences retrieved successfully",
        dietPreferencesPage.getContent(),
        HttpStatus.OK,
        meta
    );
  }

  // Obtener una preferencia de dieta por ID
  @GetMapping("/{dietPreferenceId}")
  @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
  public ResponseEntity<?> getById(@PathVariable Long dietPreferenceId, HttpServletRequest request) {
    Optional<Diet_Preferences> foundDietPreference = dietPreferenceRepository.findById(dietPreferenceId);

    if (foundDietPreference.isPresent()) {
      return new GlobalResponseHandler().handleResponse(
          "Diet preference retrieved successfully",
          foundDietPreference.get(),
          HttpStatus.OK,
          request
      );
    } else {
      return new GlobalResponseHandler().handleResponse(
          "Diet preference id " + dietPreferenceId + " not found",
          null,
          HttpStatus.NOT_FOUND,
          request
      );
    }
  }

  // Obtener una preferencia de dieta por nombre
  @GetMapping("/name/{diet_PreferenceName}")
  @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
  public ResponseEntity<?> getByName(@PathVariable String diet_PreferenceName, HttpServletRequest request) {
    Optional<Diet_Preferences> foundDietPreference = dietPreferenceRepository.findByName(diet_PreferenceName);

    if (foundDietPreference.isPresent()) {
      return new GlobalResponseHandler().handleResponse(
          "Diet preference retrieved successfully",
          foundDietPreference.get(),
          HttpStatus.OK,
          request
      );
    } else {
      return new GlobalResponseHandler().handleResponse(
          "Diet preference with name '" + diet_PreferenceName + "' not found",
          null,
          HttpStatus.NOT_FOUND,
          request
      );
    }
  }

}
