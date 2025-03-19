package com.project.demo.logic.entity.ingredient;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findByName(String name);
    List<Ingredient> findByUserId(Long userId);
}
