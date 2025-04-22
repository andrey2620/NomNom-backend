package com.project.demo.rest.shoppingList;

import com.project.demo.logic.entity.ShoppingList.ShoppingList;
import com.project.demo.logic.entity.ShoppingList.ShoppingListRepository;
import com.project.demo.logic.entity.allergies.AllergiesRepository;
import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.services.ShoppingListService;
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

  @GetMapping("/user/{userId}/list-name/{name}")
  @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
  public ResponseEntity<?> getShoppingListsByUserIdAndName(
      @PathVariable Long userId,
      @PathVariable String name,
      HttpServletRequest request) {
    try {
      List<ShoppingList> lists = shoppingListService.getShoppingListsByUserIdAndName(userId, name);

      return new GlobalResponseHandler().handleResponse(
          "Shopping lists retrieved successfully",
          lists,
          HttpStatus.OK,
          request
      );
    } catch (IllegalArgumentException e) {
      return new GlobalResponseHandler().handleResponse(
          e.getMessage(),
          null,
          HttpStatus.NOT_FOUND,
          request
      );
    } catch (Exception e) {
      return new GlobalResponseHandler().handleResponse(
          "Error retrieving shopping lists: " + e.getMessage(),
          null,
          HttpStatus.INTERNAL_SERVER_ERROR,
          request
      );
    }
  }

  @GetMapping("/user/{userId}/search")
  @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
  public ResponseEntity<?> searchShoppingListsByName(
      @PathVariable Long userId,
      @RequestParam String name,
      HttpServletRequest request) {

    try {
      List<ShoppingList> matchingLists = shoppingListService.searchShoppingListsByName(userId, name);
      return new GlobalResponseHandler().handleResponse(
          "Shopping lists filtered by name successfully",
          matchingLists,
          HttpStatus.OK,
          request
      );
    } catch (IllegalArgumentException e) {
      return new GlobalResponseHandler().handleResponse(e.getMessage(), null, HttpStatus.NOT_FOUND, request);
    } catch (Exception e) {
      return new GlobalResponseHandler().handleResponse("Error filtering shopping lists: " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR, request);
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

  @PutMapping("/{shoppingListId}/rename")
  @PreAuthorize("hasAnyRole('USER', 'SUPER_ADMIN')")
  public ResponseEntity<?> updateShoppingListName(
      @PathVariable Long shoppingListId,
      @RequestBody Map<String, Object> request,
      HttpServletRequest httpRequest) {

    try {
      String newName = request.get("name").toString();

      ShoppingList updated = shoppingListService.updateShoppingListName(shoppingListId, newName);
      return new GlobalResponseHandler().handleResponse(
          "Shopping list renamed successfully",
          updated,
          HttpStatus.OK,
          httpRequest
      );
    } catch (IllegalArgumentException e) {
      return new GlobalResponseHandler().handleResponse(e.getMessage(), null, HttpStatus.NOT_FOUND, httpRequest);
    } catch (Exception e) {
      return new GlobalResponseHandler().handleResponse("Error renaming shopping list: " + e.getMessage(), null, HttpStatus.INTERNAL_SERVER_ERROR, httpRequest);
    }
  }

}
