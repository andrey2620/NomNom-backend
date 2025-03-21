package com.project.demo.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.*;

@Service
public class RecipeGeneratorService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<String> generateRecipesFromJsonIngredients() throws Exception {
        // Leer el JSON desde resources
        InputStream is = new ClassPathResource("ingredients.json").getInputStream();
        JsonNode ingredientsArray = objectMapper.readTree(is);

        List<String> ingredientNames = new ArrayList<>();
        for (JsonNode node : ingredientsArray) {
            ingredientNames.add(node.get("name").asText());
        }

        // Construir el prompt para el modelo
        String prompt = "Usando 5 de estos ingredientes, por favor crea 6 recetas creativas y detalladas:\n" +
                String.join(", ", ingredientNames) + ".\n" +
                "Devuélveme una lista con los nombres y una pequeña descripción.";

        // Preparar request para Ollama
        Map<String, Object> body = new HashMap<>();
        body.put("model", "deepseek-r1:1.5b");
        body.put("prompt", prompt);
        body.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:11434/api/generate", entity, String.class);

        // Procesar la respuesta
        String responseBody = response.getBody();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String generatedText = jsonNode.get("response").asText();

        // Separar en recetas por líneas o ítems
        return Arrays.asList(generatedText.split("\n"));
    }
}