package com.project.demo.logic.entity.ingredient;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findByName(String name);
    List<Ingredient> findByNameContaining(String name);
    Page<Ingredient> findByNameContaining(String name, Pageable pageable);
}
