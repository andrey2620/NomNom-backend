package com.project.demo.logic.entity.recipe;

public class RecipeIngredientDTO {
    private Long id;
    private Long ingredientId;
    private Long recipeId;
    private String quantity;
    private String measurement;

    public RecipeIngredientDTO(Long id, Long ingredientId, Long recipeId, String quantity, String measurement) {
        this.id = id;
        this.ingredientId = ingredientId;
        this.recipeId = recipeId;
        this.quantity = quantity;
        this.measurement = measurement;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Long ingredientId) {
        this.ingredientId = ingredientId;
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }
}
