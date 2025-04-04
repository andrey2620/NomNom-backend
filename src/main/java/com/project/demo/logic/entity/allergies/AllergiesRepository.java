package com.project.demo.logic.entity.allergies;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AllergiesRepository extends JpaRepository<Allergies, Long> {
  Optional<Allergies> findByName(String name);
}
