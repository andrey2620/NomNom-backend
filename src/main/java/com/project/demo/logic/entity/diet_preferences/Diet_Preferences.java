package com.project.demo.logic.entity.diet_preferences;
import com.project.demo.logic.entity.user.User;
import jakarta.persistence.*;

@Entity
public class Diet_Preferences {
  @Id
  private Long id;

  @ManyToOne
  private User user;

  private String name;

  public Diet_Preferences(String name, User user) {
    this.name = name;
    this.user = user;
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

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
