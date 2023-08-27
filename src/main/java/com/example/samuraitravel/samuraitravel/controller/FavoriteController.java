package com.example.samuraitravel.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.samuraitravel.samuraitravel.entity.Favorite;
import com.example.samuraitravel.samuraitravel.entity.User;
import com.example.samuraitravel.samuraitravel.repository.FavoriteRepository;
import com.example.samuraitravel.samuraitravel.security.UserDetailsImpl;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {

  private final FavoriteRepository favoriteRepository;

  public FavoriteController(FavoriteRepository favoriteRepository) {
    this.favoriteRepository = favoriteRepository;
  }
  
  @GetMapping
  public String index(
    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable,
    Model model
  ) {

    User user = userDetailsImpl.getUser();
    Page<Favorite> favorites = this.favoriteRepository.findByUserOrderByCreatedAtDesc(user, pageable);

    model.addAttribute("favorites", favorites);

    return "favorites/index";
  }

}
