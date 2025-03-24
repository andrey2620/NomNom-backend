package com.project.demo.logic.entity.diet_preferences;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface Diet_PreferenceRepository extends JpaRepository<Diet_Preferences, Long> {
  List<Diet_Preferences> findByUser_Id(Long userId);
  Optional<Diet_Preferences> findByName(String name);
}
