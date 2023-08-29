package com.example.samuraitravel.samuraitravel.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.samuraitravel.entity.Favorite;
import com.example.samuraitravel.samuraitravel.entity.House;
import com.example.samuraitravel.samuraitravel.entity.User;
import com.example.samuraitravel.samuraitravel.form.FavoriteRegisterForm;
import com.example.samuraitravel.samuraitravel.repository.FavoriteRepository;
import com.example.samuraitravel.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.samuraitravel.repository.UserRepository;

@Service
public class FavoriteService {
  
  private final FavoriteRepository favoriteRepository;
  private final UserRepository userRepository;
  private final HouseRepository houseRepository;

  public FavoriteService(
    FavoriteRepository favoriteRepository,
    UserRepository userRepository,
    HouseRepository houseRepository
  ) {
    this.favoriteRepository = favoriteRepository;
    this.userRepository = userRepository;
    this.houseRepository = houseRepository;
  }

  @Transactional
  public void add(FavoriteRegisterForm favoriteRegisterForm) {

    User user = this.userRepository.getReferenceById(favoriteRegisterForm.getUserId());
    House house = this.houseRepository.getReferenceById(favoriteRegisterForm.getHouseId());
    Favorite favorite = new Favorite();

    favorite.setUser(user);
    favorite.setHouse(house);

    this.favoriteRepository.save(favorite);
  }

}
