package com.project.demo.logic.entity.ingredient;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Component
public class IngredientSeeder implements ApplicationListener<ContextRefreshedEvent> {
    private final IngredientRepository ingredientRepository;

    public IngredientSeeder(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.seedIngredients();
    }

    private void seedIngredients() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("ingredients.json");
            if (inputStream == null) {
                //System.out.println("No se encontró el archivo ingredients.json.");
                return;
            }

            List<Ingredient> ingredients = objectMapper.readValue(inputStream, new TypeReference<>() {});

            for (Ingredient ingredient : ingredients) {
                Optional<Ingredient> existingIngredient = ingredientRepository.findByName(ingredient.getName());
                if (existingIngredient.isEmpty()) {
                    ingredientRepository.save(ingredient);
                    //System.out.println("Ingrediente agregado: " + ingredient.getName());
                } else {
                    //System.out.println("Ingrediente ya existe: " + ingredient.getName());
                }
            }

        } catch (Exception e) {
            //System.out.println("Error al cargar los ingredientes: " + e.getMessage());
        }
    }
}
