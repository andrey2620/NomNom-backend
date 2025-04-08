package com.project.demo.logic.entity.allergies;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "allergies")
public class Allergies {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id_allergies")
    private Long id;

  @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;


  public Allergies(String name) {
      this.name = name;
    }

  public Allergies() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

}


