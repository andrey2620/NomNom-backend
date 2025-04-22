package com.project.demo.services;

import com.project.demo.logic.entity.ShoppingList.ShoppingList;
import com.project.demo.logic.entity.ShoppingList.ShoppingListItem;
import com.project.demo.logic.entity.ShoppingList.ShoppingListItemRepository;
import com.project.demo.logic.entity.ShoppingList.ShoppingListRepository;
import com.project.demo.logic.entity.ingredient.IngredientRepository;
import com.project.demo.logic.entity.recipe.Recipe;
import com.project.demo.logic.entity.recipe.RecipeIngredient;
import com.project.demo.logic.entity.recipe.RecipeIngredientRepository;
import com.project.demo.logic.entity.recipe.RecipeRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.ingredient.Ingredient;

import com.project.demo.logic.entity.user.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ShoppingListService {

  private final ShoppingListRepository shoppingListRepository;
  private final ShoppingListItemRepository shoppingListItemRepository;
  private final RecipeRepository recipeRepository;
  private final RecipeIngredientRepository recipeIngredientRepository;
  private final UserRepository userRepository;
  private final IngredientRepository ingredientRepository;

  public ShoppingListService(
      ShoppingListRepository shoppingListRepository,
      ShoppingListItemRepository shoppingListItemRepository,
      RecipeRepository recipeRepository,
      RecipeIngredientRepository recipeIngredientRepository,
      UserRepository userRepository,
      IngredientRepository ingredientRepository) {
    this.shoppingListRepository = shoppingListRepository;
    this.shoppingListItemRepository = shoppingListItemRepository;
    this.recipeRepository = recipeRepository;
    this.recipeIngredientRepository = recipeIngredientRepository;
    this.userRepository = userRepository;
    this.ingredientRepository = ingredientRepository;
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

  public void addManualItemsToList(Long shoppingListId, List<Map<String, Object>> items) {
    Optional<ShoppingList> shoppingListOpt = shoppingListRepository.findById(shoppingListId);
    if (shoppingListOpt.isEmpty()) {
      throw new IllegalArgumentException("Shopping List no encontrado");
    }

    ShoppingList shoppingList = shoppingListOpt.get();

    for (Map<String, Object> item : items) {
      Long ingredientId = Long.valueOf(item.get("ingredientId").toString());
      BigDecimal quantity = new BigDecimal(item.get("quantity").toString());
      String measurement = item.get("measurement").toString();

      Optional<Ingredient> ingredientOptional = ingredientRepository.findById(ingredientId);
      if (ingredientOptional.isEmpty()) {
        throw new IllegalArgumentException("Ingredient " + ingredientId + " no encontrado");
      }
      ShoppingListItem newItem = new ShoppingListItem();
      newItem.setIngredient(ingredientOptional.get());
      newItem.setQuantity(quantity);
      newItem.setMeasurement(measurement);
      newItem.setShoppingList(shoppingList);

      shoppingListItemRepository.save(newItem);
    }
  }

  public List<ShoppingList> getShoppingListsByUserIdAndName(Long userId, String name) {
    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      throw new IllegalArgumentException("Usuario con id " + userId + " no existe");
    }

    return shoppingListRepository.findByUserIdAndNameContainingIgnoreCase(userId, name);
  }

  public List<ShoppingList> searchShoppingListsByName(Long userId, String name) {
    Optional<User> userOpt = userRepository.findById(userId);
    if (userOpt.isEmpty()) {
      throw new IllegalArgumentException("Usuario con id " + userId + " no existe");
    }

    List<ShoppingList> allLists = shoppingListRepository.findByUserId(userId);

    return allLists.stream()
        .filter(list -> list.getName().toLowerCase().contains(name.toLowerCase()))
        .collect(Collectors.toList());
  }

  public ShoppingList updateShoppingListName(Long shoppingListId, String newName) {
    Optional<ShoppingList> shoppingListOpt = shoppingListRepository.findById(shoppingListId);

    if (shoppingListOpt.isEmpty()) {
      throw new IllegalArgumentException("Shopping list con id " + shoppingListId + " no existe");
    }

    ShoppingList shoppingList = shoppingListOpt.get();
    shoppingList.setName(newName);

    return shoppingListRepository.save(shoppingList);
  }


  public void deleteShoppingList(Long shoppingListId) {
    Optional<ShoppingList> listOpt = shoppingListRepository.findById(shoppingListId);
    if (listOpt.isEmpty()) {
      throw new IllegalArgumentException("Shopping list con id " + shoppingListId + " no encontrado");
    }

    shoppingListRepository.deleteById(shoppingListId);
  }


  // - descargarLista
}
