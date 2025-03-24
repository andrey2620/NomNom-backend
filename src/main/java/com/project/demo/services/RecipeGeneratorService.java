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

        List<String> ingredientNames = new ArrayList<>();
        for (JsonNode node : ingredientsArray) {
            ingredientNames.add(node.get("name").asText());
        }

        // Construir el prompt
        String prompt = """
                En español:
                Usando algunos de estos ingredientes:
                Crea UNA receta creativa y devuélvela en el siguiente JSON ESTRICTO:
                {
                  "name": "Nombre de la receta",
                  "preparationTime": 30,
                  "instructions": "Paso 1. Haz esto. Paso 2. Haz esto otro.",
                  "ingredients": [
                    { "name": "Ingrediente A", "quantity": "2.5", "measurement": "tazas" },
                    { "name": "Ingrediente B", "quantity": "1", "measurement": "cucharada" }
                  ]
                }
                Importante:
                - Devuelve SOLO el JSON, sin explicaciones, sin texto antes o después.
                - No uses arrays multilínea ni Markdown.
                - Las instrucciones deben ir como un solo string.
                """.formatted(String.join(", ", ingredientNames));

        // Llamar al modelo
        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama3.1:latest");
        body.put("prompt", prompt);
        body.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:11434/api/generate", entity, String.class);

        String responseBody = response.getBody();
        JsonNode fullResponse = objectMapper.readTree(responseBody);
        String json = fullResponse.get("response").asText().trim();

        // Validación rápida: ¿empieza con {?
        if (!json.startsWith("{")) {
            System.err.println("❌ Respuesta inválida, no parece JSON:");
            System.err.println(json);
            throw new RuntimeException("El modelo no devolvió un JSON válido.");
        }

        // Intentar parsear como JSON
        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            System.err.println("❌ JSON malformado recibido del modelo:");
            System.err.println(json);
            throw new RuntimeException("Error al parsear JSON generado por el modelo.", e);
        }
    }
}
