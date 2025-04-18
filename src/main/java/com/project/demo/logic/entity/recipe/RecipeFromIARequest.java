package com.project.demo.logic.entity.recipe;

import java.util.List;

public class RecipeFromIARequest {
    private String name;
    private String recipeCategory;
    private Integer preparationTime;
    private String description;
    private String nutritionalInfo;
    private String instructions;
    private List<IngredientDTO> ingredients;

    public static class IngredientDTO {
        public String name;
        public String quantity;
        public String measurement;
    }

    // Getters y setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRecipeCategory() { return recipeCategory; }
    public void setRecipeCategory(String recipeCategory) { this.recipeCategory = recipeCategory; }

    public Integer getPreparationTime() { return preparationTime; }
    public void setPreparationTime(Integer preparationTime) { this.preparationTime = preparationTime; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getNutritionalInfo() { return nutritionalInfo; }
    public void setNutritionalInfo(String nutritionalInfo) { this.nutritionalInfo = nutritionalInfo; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public List<IngredientDTO> getIngredients() { return ingredients; }
    public void setIngredients(List<IngredientDTO> ingredients) { this.ingredients = ingredients; }
}
