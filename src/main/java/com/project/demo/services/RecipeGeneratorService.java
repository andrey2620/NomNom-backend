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

    public JsonNode generateSingleRecipe() throws Exception {
        // Leer ingredientes desde el archivo JSON
        InputStream is = new ClassPathResource("ingredients.json").getInputStream();
        JsonNode ingredientsArray = objectMapper.readTree(is);

        List<String>    ingredientNames = new ArrayList<>();
        for (JsonNode node : ingredientsArray) {
            ingredientNames.add(node.get("name").asText());
        }

        // Construir el prompt
        String prompt = """
            En español:
            Usando algunos de estos ingredientes: %s
            Crea UNA receta creativa que incluya:
            - nombre de la receta
            - instrucciones detalladas
            - tiempo de preparación (en minutos)
            - una lista de ingredientes con nombre, cantidad y medida

            Devuelve ÚNICAMENTE un JSON con el siguiente formato:

            {
              "name": "Nombre de la receta",
              "preparationTime": 30,
              "instructions": "Paso a paso detallado...",
              "ingredients": [
                { "name": "Ingrediente A", "quantity": 2.5, "measurement": "cups" },
                { "name": "Ingrediente B", "quantity": 1, "measurement": "tbsp" }
              ]
            }

            No escribas nada adicional fuera del JSON.
            """.formatted(String.join(", ", ingredientNames));

        // Llamar al modelo
        Map<String, Object> body = new HashMap<>();
        body.put("model", "deepseek-r1:1.5b");
        body.put("prompt", prompt);
        body.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:11434/api/generate", entity, String.class);
        String responseBody = response.getBody();

        // Extraer el contenido JSON desde el campo "response"
        JsonNode fullResponse = objectMapper.readTree(responseBody);
        String json = fullResponse.get("response").asText();

        // Parsear la cadena como JSON (puede lanzar error si el modelo devuelve basura)
        return objectMapper.readTree(json);
    }
}
