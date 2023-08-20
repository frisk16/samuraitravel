package com.example.samuraitravel.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.samuraitravel.entity.House;
import com.example.samuraitravel.samuraitravel.form.HouseEditForm;
import com.example.samuraitravel.samuraitravel.form.HouseRegisterForm;
import com.example.samuraitravel.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.samuraitravel.service.HouseService;

@RequestMapping("/admin/houses")
@Controller
public class AdminHouseController {
  
  private final HouseRepository houseRepository;
  private final HouseService houseService;

  public AdminHouseController(HouseRepository houseRepository, HouseService houseService) {
    this.houseRepository = houseRepository;
    this.houseService = houseService;
  }

  // 民宿一覧ページ
  @GetMapping
  public String index(
    Model model,
    @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
    @RequestParam(name = "keyword", required = false) String keyword
  ) {
    Page<House> houses;

    if(keyword != null && !keyword.isEmpty()) {
      houses = this.houseRepository.findByNameLike("%" + keyword + "%", pageable);
    } else {
      houses = this.houseRepository.findAll(pageable);
    }

    model.addAttribute("houses", houses);
    model.addAttribute("keyword", keyword);

    return "admin/houses/index";
  }

  // 民宿詳細ページ
  @GetMapping("/{id}")
  public String show(@PathVariable(name = "id") Integer id, Model model) {
    House house = this.houseRepository.getReferenceById(id);

    model.addAttribute("house", house);

    return "admin/houses/show";
  }

  // 民宿登録ページ
  @GetMapping("/register")
  public String register(Model model) {
    model.addAttribute("houseRegisterForm", new HouseRegisterForm());

    return "admin/houses/register";
  }

  // （登録）民宿登録
  @PostMapping("/create")
  public String create(@ModelAttribute @Validated HouseRegisterForm houseRegisterForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    if(bindingResult.hasErrors()) {
      return "admin/houses/register";
    }

    this.houseService.create(houseRegisterForm);
    redirectAttributes.addFlashAttribute("successMessage", "民宿を登録しました。");

    return "redirect:/admin/houses";
  }

  // 民宿編集ページ
  @GetMapping("/{id}/edit")
  public String edit(@PathVariable(name = "id") Integer id, Model model) {
    House house = this.houseRepository.getReferenceById(id);
    String imageName = house.getImageName();
    HouseEditForm houseEditForm = new HouseEditForm(
      house.getId(),
      house.getName(),
      null,
      house.getDescription(),
      house.getPrice(),
      house.getCapacity(),
      house.getPostalCode(),
      house.getAddress(),
      house.getPhoneNumber()
    );

    model.addAttribute("imageName", imageName);
    model.addAttribute("houseEditForm", houseEditForm);

    return "admin/houses/edit";
  }

  // （更新）民宿更新
  @PostMapping("/{id}/update")
  public String update(@ModelAttribute @Validated HouseEditForm houseEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    if(bindingResult.hasErrors()) {
      return "admin/houses/edit";
    }

    this.houseService.update(houseEditForm);
    redirectAttributes.addFlashAttribute("successMessage", "民宿情報を編集しました。");

    return "redirect:/admin/houses";
  }

  // （削除）民宿削除
  @PostMapping("/{id}/delete")
  public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
    this.houseRepository.deleteById(id);

    redirectAttributes.addFlashAttribute("successMessage", "民宿を削除しました。");

    return "redirect:/admin/houses";
  }
}
