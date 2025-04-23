package com.project.demo.logic.entity.ShoppingList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shoppingList")
public class ShoppingList {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_shoppingList")
  private Long id;

  @Column(nullable = false, length = 150)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_user", nullable = false)
  @JsonIgnore
  private User user;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @OneToMany(mappedBy = "shoppingList", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonManagedReference
  private List<ShoppingListItem> items = new ArrayList<>();

  public ShoppingList(Long id, String name, User user, LocalDateTime createdAt) {
    this.id = id;
    this.name = name;
    this.user = user;
    this.createdAt = createdAt;
  }

  public ShoppingList() {
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public List<ShoppingListItem> getItems() {
    return items;
  }

  public void setItems(List<ShoppingListItem> items) {
    this.items = items;
  }
}
