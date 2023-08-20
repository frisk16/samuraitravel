package com.example.samuraitravel.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.samuraitravel.entity.User;
import com.example.samuraitravel.samuraitravel.form.AdminUserEditForm;
import com.example.samuraitravel.samuraitravel.repository.UserRepository;
import com.example.samuraitravel.samuraitravel.service.UserService;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {
  
  private final UserRepository userRepository;
  private final UserService userService;

  public AdminUserController(UserRepository userRepository, UserService userService) {
    this.userRepository = userRepository;
    this.userService = userService;
  }

  @GetMapping
  public String index(
    Model model,
    @RequestParam(name = "keyword", required = false) String keyword,
    @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable
  ) {

    Page<User> users;

    if(keyword != null && !keyword.isEmpty()) {
      users = this.userRepository.findByNameLikeOrFuriganaLike("%" + keyword + "%", "%" + keyword + "%", pageable);
    } else {
      users = this.userRepository.findAll(pageable);
    }

    model.addAttribute("keyword", keyword);
    model.addAttribute("users", users);

    return "admin/users/index";

  }

  @GetMapping("/{id}")
  public String show(@PathVariable(name = "id") Integer id, Model model) {
    User user = this.userRepository.getReferenceById(id);

    model.addAttribute("user", user);

    return "admin/users/show";
  }

  @GetMapping("/{id}/edit")
  public String edit(@PathVariable(name = "id") Integer id, Model model) {
    User user = this.userRepository.getReferenceById(id);

    AdminUserEditForm adminUserEditForm = new AdminUserEditForm(
      user.getId(),
      user.getName(),
      user.getFurigana(),
      user.getPostalCode(),
      user.getAddress(),
      user.getPhoneNumber(),
      user.getEmail(),
      user.getPassword(),
      user.getEnabled()
    );

    model.addAttribute("adminUserEditForm", adminUserEditForm);

    return "admin/users/edit";
  }

  @PostMapping("/adminUpdate")
  public String adminUpdate(@ModelAttribute @Validated AdminUserEditForm adminUserEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    
    if(this.userService.isEmailRegistered(adminUserEditForm.getEmail()) && this.userService.isEmailChanged(adminUserEditForm.getId(), adminUserEditForm.getEmail())) {
      FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "そのEメールアドレスは既に存在しています。");
      bindingResult.addError(fieldError);
    }
    if(bindingResult.hasErrors()) {
      return "admin/users/edit";
    }

    this.userService.adminUpdate(adminUserEditForm);

    redirectAttributes.addFlashAttribute("successMessage", "会員情報を更新しました。");

    return "redirect:/admin/users";

  }

}
