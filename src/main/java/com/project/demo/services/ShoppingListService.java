package com.project.demo.services;

import com.project.demo.logic.entity.ShoppingList.ShoppingList;
import com.project.demo.logic.entity.ShoppingList.ShoppingListItem;
import com.project.demo.logic.entity.ShoppingList.ShoppingListItemRepository;
import com.project.demo.logic.entity.ShoppingList.ShoppingListRepository;
import com.project.demo.logic.entity.ingredient.IngredientRepository;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.project.demo.logic.entity.recipe.RecipeIngredientRepository;
import com.project.demo.logic.entity.recipe.RecipeRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.ingredient.Ingredient;

import com.project.demo.logic.entity.user.UserRepository;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
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

  public List<ShoppingList> getAllShoppingLists() {
    return shoppingListRepository.findAll();
  }

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
      ShoppingListItem newItem = new ShoppingListItem();
      newItem.setShoppingList(shoppingList);

      String quantityStr = item.get("quantity").toString();
      BigDecimal quantity = new BigDecimal(quantityStr);

      String measurement = item.get("measurement") != null
          ? item.get("measurement").toString()
          : "";

      newItem.setQuantity(quantity);
      newItem.setMeasurement(measurement);

      if (item.get("ingredientId") != null) {
        Long ingredientId = Long.valueOf(item.get("ingredientId").toString());
        Optional<Ingredient> ingredientOptional = ingredientRepository.findById(ingredientId);

        if (ingredientOptional.isEmpty()) {
          throw new IllegalArgumentException("Ingredient " + ingredientId + " no encontrado");
        }

        newItem.setIngredient(ingredientOptional.get());
      } else {
        newItem.setCustomName(item.get("name").toString());
      }

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

  public byte[] generatePdfForShoppingList(Long shoppingListId) {
    Optional<ShoppingList> shoppingListOpt = shoppingListRepository.findById(shoppingListId);

    if (shoppingListOpt.isEmpty()) {
      throw new IllegalArgumentException("Shopping list con id " + shoppingListId + " no existe");
    }

    ShoppingList list = shoppingListOpt.get();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    PdfWriter writer = new PdfWriter(baos);
    PdfDocument pdfDoc = new PdfDocument(writer);
    Document document = new Document(pdfDoc);

    document.add(new Paragraph("Lista de Compras: " + list.getName()).setBold().setFontSize(16));
    document.add(new Paragraph("Fecha: " + list.getCreatedAt()));

    float[] columnWidths = {200F, 100F, 100F};
    Table table = new Table(columnWidths);
    table.addCell("Ingrediente");
    table.addCell("Cantidad");
    table.addCell("Medida");

    for (ShoppingListItem item : list.getItems()) {
      String ingredientName = item.getIngredient() != null
          ? item.getIngredient().getName()
          : item.getCustomName();
      table.addCell(ingredientName);
    }

    document.add(table);
    document.close();

    return baos.toByteArray();
  }

  public void removeItemFromShoppingList(Long shoppingListId, Long itemId) {
    Optional<ShoppingList> shoppingListOpt = shoppingListRepository.findById(shoppingListId);
    if (shoppingListOpt.isEmpty()) {
      throw new IllegalArgumentException("Shopping list con id " + shoppingListId + " no existe");
    }

    Optional<ShoppingListItem> itemOpt = shoppingListItemRepository.findById(itemId);
    if (itemOpt.isEmpty() || !itemOpt.get().getShoppingList().getId().equals(shoppingListId)) {
      throw new IllegalArgumentException("El ítem no existe o no pertenece a esta lista");
    }

    shoppingListItemRepository.deleteById(itemId);
  }

  public ShoppingListItem updateShoppingListItem(Long itemId, BigDecimal quantity, String measurement) {
    Optional<ShoppingListItem> itemOpt = shoppingListItemRepository.findById(itemId);
    if (itemOpt.isEmpty()) {
      throw new IllegalArgumentException("Item con id " + itemId + " no existe");
    }

    ShoppingListItem item = itemOpt.get();
    item.setQuantity(quantity);
    item.setMeasurement(measurement);
    return shoppingListItemRepository.save(item);
  }

}
