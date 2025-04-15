package com.project.demo.logic.entity.ingredient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "ingredient")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ingredient")
    private Long id;

    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @Column(name = "medida", length = 50)
    private String medida;

    @Column(name = "image", nullable = true)
    private String image;

    @Column(name = "category", nullable = true)
    private String category;

    public Ingredient() {}

    public Ingredient(String name, String medida, String image, String category) {
        this.name = name;
        this.medida = medida;
        this.image = image;
        this.category = category;
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

    public String getMedida() {
        return medida;
    }

    public void setMedida(String medida) {
        this.medida = medida;
    }

    public String getImage() { return image; }

    public void setImage(String image) { this.image = image; }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }
}
