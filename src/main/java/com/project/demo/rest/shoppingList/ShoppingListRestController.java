package com.project.demo.rest.shoppingList;

import com.project.demo.logic.entity.ShoppingList.ShoppingList;
import com.project.demo.logic.entity.ShoppingList.ShoppingListRepository;
import com.project.demo.logic.entity.allergies.AllergiesRepository;
import com.project.demo.logic.entity.user.User;
import com.project.demo.logic.entity.user.UserRepository;
import com.project.demo.logic.service.ShoppingListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
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

}
