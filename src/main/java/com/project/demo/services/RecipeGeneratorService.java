package com.project.demo.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.*;

@Service
public class RecipeGeneratorService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${ia.model.name:llama3.1:latest}")
    private String model;

    @Value("${ia.ingredients.file:/ingredients.json}")
    private String ingredientsFilePath;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    // Método central que llama a la IA con un prompt dado
    public JsonNode callModelWithPrompt(String prompt) throws Exception {
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("prompt", prompt);
        body.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.postForEntity("http://localhost:11434/api/generate", entity, String.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Error al conectarse con el modelo IA", e);
        }

        String responseBody = response.getBody();
        JsonNode fullResponse = objectMapper.readTree(responseBody);
        String json = fullResponse.get("response").asText().trim();

        if (!json.startsWith("{")) {
            System.err.println("Respuesta inválida del modelo:");
            System.err.println(json);
            throw new RuntimeException("El modelo no devolvió un JSON válido.");
        }

        return objectMapper.readTree(json);
    }

    // Genera receta con todos los ingredientes del JSON local
    public JsonNode generateRecipeWithAllIngredients() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(ingredientsFilePath);
        JsonNode ingredients = objectMapper.readTree(inputStream);
        List<String> ingredientNames = new ArrayList<>();

        for (JsonNode node : ingredients) {
            ingredientNames.add(node.get("name").asText());
        }

        String prompt = buildPrompt(ingredientNames);
        return callModelWithPrompt(prompt);
    }

    // Genera receta personalizada a partir del userId
    public JsonNode generateRecipeForUser(Long userId) throws Exception {
        List<String> userIngredients = getFormattedIngredientsForUser(userId);
        return generateRecipeFromList(userIngredients);
    }

    // Genera receta a partir de una lista de nombres de ingredientes
    private JsonNode generateRecipeFromList(List<String> userIngredients) throws Exception {
        String prompt = buildPrompt(userIngredients);
        return callModelWithPrompt(prompt);
    }

    // Obtiene ingredientes formateados desde el endpoint existente
    @SuppressWarnings("unchecked")
    private List<String> getFormattedIngredientsForUser(Long userId) throws Exception {
        String url = baseUrl + "/ingredients/formated/user/" + userId;

        ResponseEntity<List<Map<String, String>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("No se pudieron obtener los ingredientes del usuario.");
        }

        List<String> ingredientNames = new ArrayList<>();
        for (Map<String, String> ingredient : response.getBody()) {
            ingredientNames.add(ingredient.get("name"));
        }

        return ingredientNames;
    }

    // Prompt en español formateado con lista de ingredientes
    private String buildPrompt(List<String> ingredients) {
        return """
                En español:
                Usando algunos de estos ingredientes:
                %s
                Crea UNA receta creativa que entre en una de estas categorias:
                [Comida][Ensalada][Jugos][Postre][Panes]
                y devuélvela en el siguiente JSON ESTRICTO:
                {
                  "name": "Nombre de la receta",
                  "recipeCategory": "Categoría de la receta",
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
                """.formatted(String.join(", ", ingredients));
    }
}
