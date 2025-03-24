package com.project.demo.logic.entity.diet_preferences;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Diet_PreferenceRepository extends JpaRepository<Diet_Preferences, Long> {
  List<Diet_Preferences> findByUserId(Long userId);
}
