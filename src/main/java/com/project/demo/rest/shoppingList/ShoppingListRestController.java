package com.project.demo.rest.shoppingList;

import com.project.demo.logic.entity.ShoppingList.ShoppingList;
import com.project.demo.logic.entity.ShoppingList.ShoppingListRepository;
import com.project.demo.logic.entity.allergies.AllergiesRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.logic.service.ShoppingListService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/shoppingList")
public class ShoppingListRestController {

  private User userDetails;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ShoppingListRepository shoppingListRepository;

  @Autowired
  private ShoppingListService shoppingListService;

  @PostMapping
  public ResponseEntity<?> createShoppingList(@RequestBody Map<String, Object> request) {
    try {
      Long userId = Long.valueOf(request.get("userId").toString());
      String name = request.get("name").toString();

      ShoppingList savedList = shoppingListService.createManualShoppingList(userId, name);
      return ResponseEntity.status(HttpStatus.CREATED).body(savedList);

    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al crear la lista: " + e.getMessage());
    }
  }

  @PostMapping("/{shoppingListId}/add-Items")
  @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
  public ResponseEntity<?> addItemToShoppingList(
      @PathVariable Long shoppingListId,
      @RequestBody List<Map<String, Object>> items,
      HttpServletRequest request) {

    try {
      shoppingListService.addManualItemsToList(shoppingListId, items);
      return new GlobalResponseHandler().handleResponse(
          "Items agregada exitosamente a shopping list",
          null,
          HttpStatus.OK,
          request
      );
    } catch (IllegalArgumentException e) {
        return new GlobalResponseHandler().handleResponse(e.getMessage(), null, HttpStatus.NOT_FOUND, request);
      } catch (Exception e) {
        return new GlobalResponseHandler().handleResponse("Error adding items: " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
  }

  @GetMapping("/user/{userId}")
  @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
  public ResponseEntity<?> getShoppingListsByUserId(@PathVariable Long userId, HttpServletRequest request) {
    try {
      List<ShoppingList> lists = shoppingListService.getShoppingListsByUserId(userId);
      return new GlobalResponseHandler().handleResponse(
          "Shopping lists exitosamente recuperado",
          lists,
          HttpStatus.OK,
          request
      );
    } catch (IllegalArgumentException e) {
      return new GlobalResponseHandler().handleResponse(e.getMessage(), null, HttpStatus.NOT_FOUND, request);
    } catch (Exception e) {
      return new GlobalResponseHandler().handleResponse("Error recuperando lists: " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
  }

  @DeleteMapping("/{shoppingListId}")
  @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
  public ResponseEntity<?> deleteShoppingList(@PathVariable Long shoppingListId, HttpServletRequest request) {
    try {
      shoppingListService.deleteShoppingList(shoppingListId);
      return new GlobalResponseHandler().handleResponse(
          "Shopping list eliminada exitosamente",
          null,
          HttpStatus.OK,
          request
      );
    } catch (IllegalArgumentException e) {
      return new GlobalResponseHandler().handleResponse(e.getMessage(), null, HttpStatus.NOT_FOUND, request);
    } catch (Exception e) {
      return new GlobalResponseHandler().handleResponse("Error eliminando shopping list: " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
  }


}
