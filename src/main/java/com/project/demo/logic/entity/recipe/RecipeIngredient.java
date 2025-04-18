package com.project.demo.logic.entity.recipe;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.demo.logic.entity.ingredient.Ingredient;
import jakarta.persistence.*;
import java.math.BigDecimal;
@Entity
@Table(name = "recipe_ingredients")
public class RecipeIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    @JsonBackReference
    private Recipe recipe;


    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Column(nullable = true)
    private String quantity;

    @Column(nullable = true)
    private String measurement;

    public RecipeIngredient() {
    }

    public RecipeIngredient(Recipe recipe, Ingredient ingredient, String quantity, String measurement) {
        this.recipe = recipe;
        this.ingredient = ingredient;
        this.quantity = quantity;
        this.measurement = measurement;
    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
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