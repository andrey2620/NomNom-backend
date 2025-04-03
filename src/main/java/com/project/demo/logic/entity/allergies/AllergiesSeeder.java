package com.project.demo.logic.entity.allergies;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;


import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Component
public class AllergiesSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final AllergiesRepository allergiesRepository;

    public AllergiesSeeder(AllergiesRepository allergiesRepository) {
    this.allergiesRepository = allergiesRepository;
  }

    @Override
    public void onApplicationEvent (ContextRefreshedEvent event){
    this.seedAllergies();
  }
  private void seedAllergies() {
    ObjectMapper objectMapper = new ObjectMapper();

    try {
      InputStream inputStream = getClass().getClassLoader().getResourceAsStream("allergies.json");
      if (inputStream == null) {
        System.out.println("No se encontró el archivo allergies.json.");
        return;
      }

      List<Allergies> allergies = objectMapper.readValue(inputStream, new TypeReference<>() {});

      for (Allergies allergy : allergies) {
        Optional<Allergies> existingAllergy = allergiesRepository.findByName(allergy.getName());
        if (existingAllergy.isEmpty()) {
          allergiesRepository.save(allergy);
          System.out.println("Alergia agregada: " + allergy.getName());
        } else {
          System.out.println("Alergia ya existe: " + allergy.getName());
        }
      }

    } catch (Exception e) {
      System.out.println("Error al cargar las alergias: " + e.getMessage());
    }
  }
  }