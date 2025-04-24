package com.project.demo.logic.entity.ShoppingList;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.demo.logic.entity.ingredient.Ingredient;
import jakarta.persistence.*;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;

@Entity
@Table(name = "shoppingListItem")
public class ShoppingListItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_shoppingList_item")
  private Long id;

  @Column(name = "custom_name")
  private String customName;

  @Column(name = "custom_quantity")
  private String customQuantity;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_shopping_list", nullable = false)
  @JsonBackReference
  private ShoppingList shoppingList;

  @Nullable
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_ingredient", nullable = true)
  private Ingredient ingredient;

  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal quantity;

  @Column(nullable = false, length = 50)
  private String measurement;

  public ShoppingListItem() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Ingredient getIngredient() {
    return ingredient;
  }

  public void setIngredient(Ingredient ingredient) {
    this.ingredient = ingredient;
  }

  public BigDecimal getQuantity() {
    return quantity;
  }

  public void setQuantity(BigDecimal quantity) {
    this.quantity = quantity;
  }

  public String getMeasurement() {
    return measurement;
  }

  public void setMeasurement(String measurement) {
    this.measurement = measurement;
  }

  public ShoppingList getShoppingList() {
    return shoppingList;
  }

  public void setShoppingList(ShoppingList shoppingList) {
    this.shoppingList = shoppingList;
  }

  public String getCustomName() {
    return customName;
  }

  public void setCustomName(String customName) {
    this.customName = customName;
  }

  public String getCustomQuantity() {
    return customQuantity;
  }

  public void setCustomQuantity(String customQuantity) {
    this.customQuantity = customQuantity;
  }
}
