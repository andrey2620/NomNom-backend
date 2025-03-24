package com.project.demo.logic.entity.diet_preferences;
import jakarta.persistence.*;

@Entity
@Table(name = "diet_preferences")
public class Diet_Preferences {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;

  public Diet_Preferences(String name) {
    this.name = name;
  }

  public Diet_Preferences() {
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
}
