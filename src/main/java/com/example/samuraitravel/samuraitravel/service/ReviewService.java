package com.example.samuraitravel.samuraitravel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.samuraitravel.entity.House;
import com.example.samuraitravel.samuraitravel.entity.Review;
import com.example.samuraitravel.samuraitravel.entity.User;
import com.example.samuraitravel.samuraitravel.form.ReviewRegisterForm;
import com.example.samuraitravel.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.samuraitravel.repository.UserRepository;

@Service
public class ReviewService {
  
  private final ReviewRepository reviewRepository;
  private final HouseRepository houseRepository;
  private final UserRepository userRepository;

  public ReviewService(
    ReviewRepository reviewRepository,
    HouseRepository houseRepository,
    UserRepository userRepository
  ) {
    this.reviewRepository = reviewRepository;
    this.houseRepository = houseRepository;
    this.userRepository = userRepository;
  }

  @Transactional
  public void create(ReviewRegisterForm reviewRegisterForm) {

    House house = this.houseRepository.getReferenceById(reviewRegisterForm.getHouseId());
    User user = this.userRepository.getReferenceById(reviewRegisterForm.getUserId());
    Review review = new Review();
    
    review.setHouse(house);
    review.setUser(user);
    review.setName(user.getName());
    review.setScore(reviewRegisterForm.getScore());
    review.setComment(reviewRegisterForm.getComment());

    this.reviewRepository.save(review);
  }

}
