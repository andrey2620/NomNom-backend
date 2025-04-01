package com.project.demo.services;

import com.project.demo.logic.entity.recipe.RecipeIngredient;
import com.project.demo.logic.entity.recipe.RecipeIngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.util.Optional;

@Service
public class RecipeIngredientService {

    @Autowired
    private RecipeIngredientRepository recipeIngredientRepository;

    public Page<RecipeIngredient> getAll(Pageable pageable) {
        return recipeIngredientRepository.findAll(pageable);
    }

    public Optional<RecipeIngredient> getById(Long id) {
        return recipeIngredientRepository.findById(id);
    }

    public RecipeIngredient save(RecipeIngredient entity) {
        return recipeIngredientRepository.save(entity);
    }

    public void delete(Long id) {
        recipeIngredientRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return recipeIngredientRepository.existsById(id);
    }
}