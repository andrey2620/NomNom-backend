package com.project.demo.logic.entity.allergies;

import com.project.demo.logic.entity.diet_preferences.Diet_Preferences;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AllergiesRepository extends JpaRepository<Allergies, Long> {
  List<Allergies> findByUser_Id(Long userId);
  Optional<Allergies> findByName(String name);
}
