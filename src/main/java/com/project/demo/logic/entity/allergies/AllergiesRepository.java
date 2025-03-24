package com.project.demo.logic.entity.allergies;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AllergiesRepository extends JpaRepository<Allergies, Long> {
  List<Allergies> findByUserId(Long userId);
  }

