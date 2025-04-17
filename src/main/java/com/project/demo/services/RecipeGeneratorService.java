package com.project.demo.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.demo.logic.entity.auth.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.base-url-ollama:http://localhost:11434}")
    private String baseUrlOllama;

    public JsonNode callModelWithPrompt(String prompt) throws Exception {
        if (prompt == null || prompt.trim().isEmpty()) {
            throw new IllegalArgumentException("El prompt no puede estar vacío.");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("prompt", prompt);
        body.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.postForEntity(baseUrlOllama + "/api/generate", entity, String.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Error al conectarse con el modelo IA", e);
        }

        String responseBody = response.getBody();
        JsonNode fullResponse = objectMapper.readTree(responseBody);
        String json = fullResponse.get("response").asText().trim();

        try {
            return objectMapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("JSON inválido generado por el modelo. Respuesta: " + json, e);
        }
    }

    public Map<String, Object> generateRecipeWithAllIngredients() throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(ingredientsFilePath);
        JsonNode ingredients = objectMapper.readTree(inputStream);
        List<String> ingredientNames = new ArrayList<>();

        for (JsonNode node : ingredients) {
            ingredientNames.add(node.get("name").asText());
        }

        return generateRecipeFromList(ingredientNames);
    }


    public Map<String, Object> generateRecipeForUser(Long userId) throws Exception {
        List<String> userIngredients = getFormattedIngredientsForUser(userId);
        if (userIngredients == null || userIngredients.isEmpty()) {
            throw new IllegalStateException("No se encontraron ingredientes para el usuario " + userId);
        }
        return generateRecipeFromList(userIngredients);
    }


    public Map<String, Object> generateRecipeFromList(List<String> userIngredients) throws Exception {
        String prompt = buildPrompt(userIngredients);
        JsonNode recipe = callModelWithPrompt(prompt);

        Map<String, Object> result = new HashMap<>();
        result.put("prompt", prompt);
        result.put("recipe", recipe);
        return result;
    }


    private List<String> getFormattedIngredientsForUser(Long userId) throws Exception {
        String url = baseUrl + "/ingredients/formated/user/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + JwtTokenUtils.getCurrentToken());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                JsonNode.class
        );

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("No se pudieron obtener los ingredientes del usuario.");
        }

        JsonNode dataNode = response.getBody().get("data");
        if (dataNode == null || !dataNode.isArray()) {
            throw new RuntimeException("La estructura de respuesta no es válida.");
        }

        List<String> ingredientNames = new ArrayList<>();
        for (JsonNode node : dataNode) {
            ingredientNames.add(node.asText());
        }

        return ingredientNames;
    }

    private String buildPrompt(List<String> ingredients) {
        return """
                En español:
                Usando algunos de estos ingredientes:
                %s
                Crea UNA receta creativa que entre en una de estas categorias:
                [Comida][Ensalada][Jugos][Postre][Panes]
                y devuélvela en el siguiente JSON ESTRICTO:
                que los pasos esten separados unicamente por '.' y no la palabra paso
                {
                  "name": "Nombre de la receta",
                  "recipeCategory": "Categoría de la receta",
                  "preparationTime": 30,
                  "description": "Descripción corta de la receta.",
                  "nutritionalInfo:": "20cal, 5g proteínas, 10g carbohidratos, 5g grasas",
                  "instructions": "Haz esto. Haz esto otro. Haz esto otro.",
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

    public JsonNode getSuggestionsForRecipe(Map<String, Object> recipeJson) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String recipeString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(recipeJson);

        String prompt = """
                En español:
                A partir de esta receta:
                %s
                
                Da las siguientes sugerencias:
                1. Sustituciones saludables o accesibles para los ingredientes si es necesario.
                2. Consejos creativos de presentación para servir el platillo.
                3. Momentos seguros dentro de la preparación donde pueden participar los niños.
                
                Devuélvelo en este formato JSON ESTRICTO:
                {
                  "ingredientSubstitutions": ["texto 1", "texto 2"],
                  "presentationTips": ["texto 1", "texto 2"],
                  "kidsParticipation": ["texto 1", "texto 2"]
                }
                
                Importante:
                - Solo devuelve el JSON.
                - No expliques el resultado ni uses texto adicional.
                - Sin markdown ni encabezados.
                """.formatted(recipeString);

        return callModelWithPrompt(prompt);
    }

    public Map<String, Object> generateRecipeFromIngredients(List<String> ingredients) throws Exception {
        if (ingredients == null || ingredients.isEmpty()) {
            throw new IllegalArgumentException("La lista de ingredientes no puede estar vacía.");
        }
        return generateRecipeFromList(ingredients);
    }


}