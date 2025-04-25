package com.project.demo.logic.entity.diet_preferences;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "diet_preferences")
public class Diet_Preferences {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_preferences")
  private Long id;

  @Column(name = "name", length = 100, nullable = false, unique = true)
  private String name;

  public Diet_Preferences(String name, User user) {
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
