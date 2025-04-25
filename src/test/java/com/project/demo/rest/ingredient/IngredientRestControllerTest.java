package com.project.demo.rest.ingredient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.demo.logic.entity.ingredient.Ingredient;
import com.project.demo.services.IngredientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class IngredientRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IngredientService ingredientService;

    @Autowired
    private ObjectMapper objectMapper;

    private Ingredient testIngredient;
    private Long userId = 1L;
    private Long ingredientId = 1L;

    @BeforeEach
    void setUp() {
        testIngredient = new Ingredient();
        testIngredient.setId(ingredientId);
        testIngredient.setName("Tomate");
        testIngredient.setCategory("Verdura");
    }

    @Test
    @WithMockUser(roles = "USER")
    void getIngredientById_ExistingId_ReturnsIngredient() throws Exception {
        // Arrange
        when(ingredientService.getIngredientById(ingredientId)).thenReturn(Optional.of(testIngredient));

        // Act & Assert
        MvcResult result = mockMvc.perform(get("/ingredients/{id}", ingredientId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testIngredient.getId().intValue())))
                .andExpect(jsonPath("$.name", is(testIngredient.getName())))
                .andReturn(); // Captura el resultado
    
        // Obtener y mostrar la respuesta para documentación
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Respuesta JSON para getIngredientById: " + responseContent);
    }

    @Test
    @WithMockUser(roles = "USER")
    void getUserIngredients_ValidUserId_ReturnsIngredientsList(TestInfo testInfo) throws Exception {
        // Documentación de la prueba
        System.out.println("Ejecutando prueba: " + testInfo.getDisplayName());
        System.out.println("Endpoint: /ingredients/formated/user/{userId}");
        System.out.println("Método: GET");
        System.out.println("Parámetros: userId=" + userId);
        
        // Arrange
        List<Map<String, Object>> userIngredients = new ArrayList<>();
        Map<String, Object> ingredientMap = new HashMap<>();
        ingredientMap.put("id", ingredientId);
        ingredientMap.put("name", testIngredient.getName());
        userIngredients.add(ingredientMap);
    
        when(ingredientService.getFormattedIngredientsByUserId(userId)).thenReturn(userIngredients);
    
        // Act & Assert
        MvcResult result = mockMvc.perform(get("/ingredients/formated/user/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id", is(ingredientId.intValue())))
                .andExpect(jsonPath("$[0].name", is(testIngredient.getName())))
                .andReturn();
                
        // Obtener y mostrar la respuesta para documentación
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Respuesta esperada: " + responseContent);
        System.out.println("Código de estado: " + result.getResponse().getStatus());
    }

    @Test
    @WithMockUser(roles = "USER")
    void linkIngredientToUser_ValidIds_ReturnsSuccess() throws Exception {
        // Arrange
        String successMessage = "Ingrediente vinculado correctamente al usuario.";
        when(ingredientService.linkExistingIngredientToUser(ingredientId, userId)).thenReturn(successMessage);

        // Act & Assert
        mockMvc.perform(post("/ingredients/link/{ingredientId}/user/{userId}", ingredientId, userId))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print()) // This will print the actual response
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", is(successMessage)));
    }
}