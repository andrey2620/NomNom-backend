package com.project.demo.logic.entity.recipe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "recipe")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_recipe")
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "recipe_category", nullable = false)
    private RecipeCategory recipeCategory;

    @Column(name = "preparation_time", nullable = false)
    private Integer preparationTime;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "nutritional_info", nullable = false, columnDefinition = "TEXT")
    private String nutritionalInfo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String instructions;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();

    public Recipe(Long id, String name, RecipeCategory recipeCategory, Integer preparationTime, String description, String nutritionalInfo, String instructions, List<RecipeIngredient> recipeIngredients) {
        this.id = id;
        this.name = name;
        this.recipeCategory = recipeCategory;
        this.preparationTime = preparationTime;
        this.description = description;
        this.nutritionalInfo = nutritionalInfo;
        this.instructions = instructions;
        this.recipeIngredients = recipeIngredients;
    }

    public Recipe() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RecipeCategory getRecipeCategory() {
        return recipeCategory;
    }

    public void setRecipeCategory(RecipeCategory recipeCategory) {
        this.recipeCategory = recipeCategory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Integer getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(Integer preparationTime) {
        this.preparationTime = preparationTime;
    }

    public String getNutritionalInfo() {
        return nutritionalInfo;
    }

    public void setNutritionalInfo(String nutritionalInfo) {
        this.nutritionalInfo = nutritionalInfo;
    }

    public void setRecipeIngredients(List<RecipeIngredient> recipeIngredients) {
        this.recipeIngredients = recipeIngredients;
    }
    public List<RecipeIngredient> getRecipeIngredients() {
        return recipeIngredients;
    }

}
