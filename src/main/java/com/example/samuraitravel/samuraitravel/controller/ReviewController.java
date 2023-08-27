package com.example.samuraitravel.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.samuraitravel.entity.House;
import com.example.samuraitravel.samuraitravel.entity.Review;
import com.example.samuraitravel.samuraitravel.entity.User;
import com.example.samuraitravel.samuraitravel.form.ReviewEditForm;
import com.example.samuraitravel.samuraitravel.form.ReviewRegisterForm;
import com.example.samuraitravel.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.samuraitravel.repository.ReviewRepository;
import com.example.samuraitravel.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.samuraitravel.service.ReviewService;

@Controller
public class ReviewController {
  
  private final ReviewRepository reviewRepository;
  private final HouseRepository houseRepository;
  private final ReviewService reviewService;

  public ReviewController(
    ReviewRepository reviewRepository,
    HouseRepository houseRepository,
    ReviewService reviewService
  ) {
    this.reviewRepository = reviewRepository;
    this.houseRepository = houseRepository;
    this.reviewService = reviewService;
  }

  @GetMapping("/houses/{id}/reviews")
  public String index(
    @PathVariable(name = "id") Integer id,
    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    @PageableDefault(page = 0, size = 10,sort = "id" , direction = Direction.ASC) Pageable pageable,
    Model model
  ) {

    House house = this.houseRepository.getReferenceById(id);
    Page<Review> reviews = this.reviewRepository.findByHouseOrderByCreatedAtDesc(house, pageable);

    Integer currentUserId;
    if(userDetailsImpl != null) {
      currentUserId = userDetailsImpl.getUser().getId();
    } else {
      currentUserId = 0;
    }
    
    model.addAttribute("reviews", reviews);
    model.addAttribute("house", house);
    model.addAttribute("currentUserId", currentUserId);

    return "reviews/index";
  }

  @GetMapping("/houses/{id}/reviews/register")
  public String register(
    @PathVariable(name = "id") Integer id,
    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    Model model
  ) {

    House house = this.houseRepository.getReferenceById(id);
    User user = userDetailsImpl.getUser();
    Review review = this.reviewRepository.findByHouseAndUser(house, user);

    if(review != null) {
      return "redirect:/";
    }

    ReviewRegisterForm reviewRegisterForm = new ReviewRegisterForm(
      house.getId(),
      user.getId(),
      null,
      null
    );

    model.addAttribute("house", house);
    model.addAttribute("reviewRegisterForm", reviewRegisterForm);

    return "reviews/register";
  }

  @PostMapping("/houses/{id}/reviews/create")
  public String create(
    @PathVariable(name = "id") Integer id,
    @ModelAttribute @Validated ReviewRegisterForm reviewRegisterForm,
    BindingResult bindingResult,
    RedirectAttributes redirectAttributes,
    Model model
  ) {

    House house = this.houseRepository.getReferenceById(id);
    
    if(bindingResult.hasErrors()) {
      model.addAttribute("house", house);
      model.addAttribute("errorMessage", "入力内容に誤りがあります。");
      return "reviews/register";
    }

    this.reviewService.create(reviewRegisterForm);    

    redirectAttributes.addFlashAttribute("successMessage", "レビューを投稿しました。");
  
    return "redirect:/houses/{id}";
  }

  @GetMapping("/houses/{id}/reviews/edit")
  public String edit(
    @PathVariable(name = "id") Integer id,
    @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
    Model model
  ) {

    User user = userDetailsImpl.getUser();
    House house = this.houseRepository.getReferenceById(id);
    Review review = this.reviewRepository.findByHouseAndUser(house, user);

    ReviewEditForm reviewEditForm = new ReviewEditForm(
      review.getId(),
      house.getId(),
      user.getId(),
      review.getScore(),
      review.getComment()
    );

    model.addAttribute("house", house);
    model.addAttribute("reviewEditForm", reviewEditForm);

    return "reviews/edit";
  }

  @PostMapping("/houses/{id}/reviews/update")
  public String update(
    @PathVariable(name = "id") Integer id,
    @ModelAttribute @Validated ReviewEditForm reviewEditForm,
    BindingResult bindingResult, RedirectAttributes redirectAttributes,
    Model model
  ) {

    House house = this.houseRepository.getReferenceById(id);
    
    if(bindingResult.hasErrors()) {
      model.addAttribute("house", house);
      model.addAttribute("errorMessage", "入力内容に誤りがあります");

      return "reviews/edit";
    }

    this.reviewService.update(reviewEditForm);

    redirectAttributes.addFlashAttribute("successMessage", "レビューを更新しました");

    return "redirect:/houses/{id}";
  }

  @PostMapping("/houses/{houseId}/reviews/{reviewId}/delete")
  public String delete(
    @PathVariable(name = "houseId") Integer houseId,
    @PathVariable(name = "reviewId") Integer reviewId,
    RedirectAttributes redirectAttributes
  ) {

    this.reviewRepository.deleteById(reviewId);

    redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました");

    return "redirect:/houses/{houseId}";
  }

}
