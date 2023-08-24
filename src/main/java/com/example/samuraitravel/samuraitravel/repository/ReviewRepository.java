package com.example.samuraitravel.samuraitravel.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.samuraitravel.entity.House;
import com.example.samuraitravel.samuraitravel.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
  
  public List<Review> findByHouse(House house);
  public List<Review> findTop6ByHouseOrderByCreatedAtDesc(House house);
  public Page<Review> findByHouseOrderByCreatedAtDesc(House house, Pageable pageable);

}
