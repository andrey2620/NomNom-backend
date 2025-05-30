package com.project.demo.logic.entity.diet_preferences;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Component
public class Diet_PreferencesSeeder implements ApplicationListener<ContextRefreshedEvent> {

  private final Diet_PreferenceRepository dietPreferencesRepository;
  private final UserRepository userRepository;

  public Diet_PreferencesSeeder(Diet_PreferenceRepository dietPreferencesRepository, UserRepository userRepository) {
    this.dietPreferencesRepository = dietPreferencesRepository;
    this.userRepository = userRepository;
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    this.seedDietPreferences();
  }

  private void seedDietPreferences() {
    ObjectMapper objectMapper = new ObjectMapper();

    try {
      InputStream inputStream = getClass().getClassLoader().getResourceAsStream("diet_preferences.json");
      if (inputStream == null) {
        //System.out.println("No se encontró el archivo diet_preferences.json.");
        return;
      }

      List<Diet_Preferences> dietPreferences = objectMapper.readValue(inputStream, new TypeReference<>() {});

      for (Diet_Preferences preference : dietPreferences) {
        Optional<Diet_Preferences> existingPreference = dietPreferencesRepository.findByName(preference.getName());
        if (existingPreference.isEmpty()) {
          dietPreferencesRepository.save(preference);
          //System.out.println("Preferencia de dieta agregada: " + preference.getName());
        } else {
          //System.out.println("Preferencia de dieta ya existe: " + preference.getName());
        }
      }

    } catch (Exception e) {
      //System.out.println("Error al cargar las preferencias de dieta: " + e.getMessage());
    }
  }
}

