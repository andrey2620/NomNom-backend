package com.project.demo.logic.entity.ShoppingList;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShoppingListRepository extends JpaRepository<ShoppingList, Long> {
  List<ShoppingList> findByUserId(Long userId);
  List<ShoppingList> findByUserIdAndNameContainingIgnoreCase(Long userId, String name);

}
