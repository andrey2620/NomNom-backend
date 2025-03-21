package com.project.demo.logic.entity.recipe;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "recipe")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recipe")
    private Long id;

    @Column(name = "name", length = 150, nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "instructions", columnDefinition = "TEXT", nullable = false)
    private String instructions;

    @Column(name = "preparation_time", nullable = false)
    private int preparationTime;

    @Column(name = "nutritional_info", columnDefinition = "TEXT", nullable = false)
    private String nutritionalInfo;

    @Column(name = "image_url", length = 255, nullable = false)
    private String imageUrl;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<RecipeIngredient> ingredients;

    // Getters y setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public int getPreparationTime() { return preparationTime; }
    public void setPreparationTime(int preparationTime) { this.preparationTime = preparationTime; }

    public String getNutritionalInfo() { return nutritionalInfo; }
    public void setNutritionalInfo(String nutritionalInfo) { this.nutritionalInfo = nutritionalInfo; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<RecipeIngredient> getIngredients() { return ingredients; }
    public void setIngredients(List<RecipeIngredient> ingredients) { this.ingredients = ingredients; }
}
