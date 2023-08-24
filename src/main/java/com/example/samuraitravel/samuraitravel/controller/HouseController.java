package com.example.samuraitravel.samuraitravel.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.samuraitravel.samuraitravel.entity.House;
import com.example.samuraitravel.samuraitravel.entity.Review;
import com.example.samuraitravel.samuraitravel.form.ReservationInputForm;
import com.example.samuraitravel.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.samuraitravel.repository.ReviewRepository;

@Controller
@RequestMapping("/houses")
public class HouseController {
  
  private final HouseRepository houseRepository;
  private final ReviewRepository reviewRepository;

  public HouseController(HouseRepository houseRepository, ReviewRepository reviewRepository) {
    this.houseRepository = houseRepository;
    this.reviewRepository = reviewRepository;
  }

  @GetMapping
  public String index(
    Model model,
    @RequestParam(name = "keyword", required = false) String keyword,
    @RequestParam(name = "area", required = false) String area,
    @RequestParam(name = "price", required = false) Integer price,
    @RequestParam(name = "order", required = false) String order,
    @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable
  ) {

    Page<House> housePage;

    if(keyword != null && !keyword.isEmpty()) {
      if(order != null && order.equals("priceAsc")) {
        housePage = this.houseRepository.findByNameLikeOrAddressLikeOrderByPriceAsc("%" + keyword + "%", "%" + keyword + "%", pageable);
      } else {
        housePage = this.houseRepository.findByNameLikeOrAddressLikeOrderByCreatedAtDesc("%" + keyword + "%", "%" + keyword + "%", pageable);
      }
    } else if(area != null && !area.isEmpty()) {
      if(order != null && order.equals("priceAsc")) {
        housePage = this.houseRepository.findByAddressLikeOrderByPriceAsc("%" + area + "%", pageable);
      } else {
        housePage = this.houseRepository.findByAddressLikeOrderByCreatedAtDesc("%" + area + "%", pageable);
      }
    } else if(price != null) {
      if(order != null && order.equals("priceAsc")) {
        housePage = this.houseRepository.findByPriceLessThanEqualOrderByPriceAsc(price, pageable);
      } else {
        housePage = this.houseRepository.findByPriceLessThanEqualOrderByCreatedAtDesc(price, pageable);
      }
    } else {
      if(order != null && order.equals("priceAsc")) {
        housePage = this.houseRepository.findAllByOrderByPriceAsc(pageable);
      } else {
        housePage = this.houseRepository.findAllByOrderByCreatedAtDesc(pageable);
      }
    }

    model.addAttribute("keyword", keyword);
    model.addAttribute("area", area);
    model.addAttribute("price", price);
    model.addAttribute("order", order);
    model.addAttribute("housePage", housePage);
   
    return "houses/index";
  }

  @GetMapping("/{id}")
  public String show(@PathVariable(name = "id") Integer id, Model model) {
    House house = this.houseRepository.getReferenceById(id);
    List<Review> reviews = this.reviewRepository.findTop6ByHouseOrderByCreatedAtDesc(house);
    Integer totalReviews = this.reviewRepository.findByHouse(house).size();

    model.addAttribute("house", house);
    model.addAttribute("reservationInputForm", new ReservationInputForm());
    model.addAttribute("reviews", reviews);
    model.addAttribute("totalReviews", totalReviews);

    return "houses/show";
  }

}
