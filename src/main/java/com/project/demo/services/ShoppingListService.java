package com.project.demo.logic.service;

import com.project.demo.logic.entity.ShoppingList.ShoppingList;
import com.project.demo.logic.entity.ShoppingList.ShoppingListItem;
import com.project.demo.logic.entity.ShoppingList.ShoppingListItemRepository;
import com.project.demo.logic.entity.ShoppingList.ShoppingListRepository;
import com.project.demo.logic.entity.recipe.Recipe;
import com.project.demo.logic.entity.recipe.RecipeIngredient;
import com.project.demo.logic.entity.recipe.RecipeIngredientRepository;
import com.project.demo.logic.entity.recipe.RecipeRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.ingredient.Ingredient;

import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ShoppingListService {

  private final ShoppingListRepository shoppingListRepository;
  private final ShoppingListItemRepository shoppingListItemRepository;
  private final RecipeRepository recipeRepository;
  private final RecipeIngredientRepository recipeIngredientRepository;
  private final UserRepository userRepository;

  public ShoppingListService(
      ShoppingListRepository shoppingListRepository,
      ShoppingListItemRepository shoppingListItemRepository,
      RecipeRepository recipeRepository,
      RecipeIngredientRepository recipeIngredientRepository,
      UserRepository userRepository
  ) {
    this.shoppingListRepository = shoppingListRepository;
    this.shoppingListItemRepository = shoppingListItemRepository;
    this.recipeRepository = recipeRepository;
    this.recipeIngredientRepository = recipeIngredientRepository;
    this.userRepository = userRepository;
  }

  // Aquí irán los métodos como:
  public ShoppingList createManualShoppingList(Long userId, String name) {
    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      throw new IllegalArgumentException("Usuario no encontrado");
    }

    ShoppingList newList = new ShoppingList();
    newList.setName(name);
    newList.setUser(userOpt.get());

    return shoppingListRepository.save(newList);
  }

  // - crearListaDesdeRecetas
  // - obtenerListasPorUsuario
  // - eliminarLista
  // - descargarLista
}
