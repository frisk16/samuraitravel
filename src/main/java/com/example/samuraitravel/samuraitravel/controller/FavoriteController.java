package com.example.samuraitravel.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.samuraitravel.entity.Favorite;
import com.example.samuraitravel.samuraitravel.entity.User;
import com.example.samuraitravel.samuraitravel.form.FavoriteRegisterForm;
import com.example.samuraitravel.samuraitravel.repository.FavoriteRepository;
import com.example.samuraitravel.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.samuraitravel.service.FavoriteService;

@Controller
public class FavoriteController {

  private final FavoriteRepository favoriteRepository;
  private final FavoriteService favoriteService;

  public FavoriteController(FavoriteRepository favoriteRepository, FavoriteService favoriteService) {
    this.favoriteRepository = favoriteRepository;
    this.favoriteService = favoriteService;
  }
  
  @GetMapping("/favorites")
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

  @PostMapping("/houses/{id}/favorites/add")
  public String add(
    @PathVariable(name = "id") Integer id,
    @ModelAttribute FavoriteRegisterForm favoriteRegisterForm,
    RedirectAttributes redirectAttributes
  ) {

    this.favoriteService.add(favoriteRegisterForm);

    redirectAttributes.addFlashAttribute("successMessage", "お気に入りに追加しました");

    return "redirect:/houses/{id}";
  }

  @PostMapping("/houses/{houseId}/favorites/{favoriteId}/delete")
  public String delete(
    @PathVariable(name = "houseId") Integer houseId,
    @PathVariable(name = "favoriteId") Integer favoriteId,
    RedirectAttributes redirectAttributes
  ) {

    this.favoriteRepository.deleteById(favoriteId);

    redirectAttributes.addFlashAttribute("successMessage", "お気に入りを解除しました");

    return "redirect:/houses/{houseId}";
  }

}
