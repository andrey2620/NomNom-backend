package com.project.demo.logic.entity.diet_preferences;
import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;

@Entity
public class Diet_Preferences {
  @Id
  private Long id;

  private String name;

  private boolean isSelected;

  public Diet_Preferences(String name, User user) {
    this.name = name;

  }

  public Diet_Preferences() {
  }

  public boolean isSelected() {
    return isSelected;
  }

  public void setSelected(boolean selected) {
    isSelected = selected;
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
