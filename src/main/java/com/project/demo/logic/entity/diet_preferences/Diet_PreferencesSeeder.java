package com.project.demo.logic.entity.diet_preferences;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Component
public class Diet_PreferencesSeeder implements ApplicationListener<ContextRefreshedEvent> {

  private final Diet_PreferenceRepository dietPreferencesRepository;

  public Diet_PreferencesSeeder(Diet_PreferenceRepository dietPreferencesRepository) {
    this.dietPreferencesRepository = dietPreferencesRepository;
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    this.seedDietPreferences();
  }

  private void seedDietPreferences() {
    ObjectMapper objectMapper = new ObjectMapper();

    try {
      // Cargar el archivo diet_preferences.json desde el directorio de recursos
      InputStream inputStream = getClass().getClassLoader().getResourceAsStream("diet_preferences.json");
      if (inputStream == null) {
        System.out.println("No se encontró el archivo diet_preferences.json.");
        return;
      }

      // Leer el archivo JSON y mapearlo a una lista de objetos Diet_Preferences
      List<Diet_Preferences> dietPreferencesList = objectMapper.readValue(inputStream, new TypeReference<>() {});

      // Verificar si los registros ya existen en la base de datos
      for (Diet_Preferences dietPreference : dietPreferencesList) {
        Optional<Diet_Preferences> existingDietPreference = dietPreferencesRepository.findByName(dietPreference.getName());
        if (existingDietPreference.isEmpty()) {
          // Si no existe, guardar el nuevo registro
          dietPreferencesRepository.save(dietPreference);
          System.out.println("Preferencia de dieta agregada: " + dietPreference.getName());
        } else {
          System.out.println("Preferencia de dieta ya existe: " + dietPreference.getName());
        }
      }

    } catch (Exception e) {
      System.out.println("Error al cargar las preferencias de dieta: " + e.getMessage());
    }
  }
}
