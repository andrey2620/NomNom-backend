package com.project.demo.logic.entity.allergies;
import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;

@Entity
public class Allergies {

    @Id
    private Long id;

    @ManyToOne
    private User user;

    private String name;

    private boolean isSelected;

  public Allergies(String name) {
      this.name = name;
      this.user = user;
      this.isSelected = true;
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

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
  }
}


