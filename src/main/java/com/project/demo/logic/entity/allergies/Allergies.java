package com.project.demo.logic.entity.allergies;
import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;

@Entity
@Table(name = "allergies")
public class Allergies {

    @Id
    private Long id;

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


