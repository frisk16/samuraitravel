package com.example.samuraitravel.samuraitravel.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.samuraitravel.entity.User;
import com.example.samuraitravel.samuraitravel.form.UserEditForm;
import com.example.samuraitravel.samuraitravel.repository.UserRepository;
import com.example.samuraitravel.samuraitravel.security.UserDetailsImpl;
import com.example.samuraitravel.samuraitravel.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
  
  private final UserRepository userRepository;
  private final UserService userService;

  public UserController(UserRepository userRepository, UserService userService) {
    this.userRepository = userRepository;
    this.userService = userService;
  }

  @GetMapping
  public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
    User user = this.userRepository.getReferenceById(userDetailsImpl.getUser().getId());

    model.addAttribute("user", user);

    return "user/index";
  }

  @GetMapping("/edit")
  public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
    User user = this.userRepository.getReferenceById(userDetailsImpl.getUser().getId());

    UserEditForm userEditForm = new UserEditForm(
      user.getId(),
      user.getName(),
      user.getFurigana(),
      user.getPostalCode(),
      user.getAddress(),
      user.getPhoneNumber(),
      user.getEmail()
    );

    model.addAttribute("userEditForm", userEditForm);

    return "user/edit";
  }

  @PostMapping("/update")
  public String update(@ModelAttribute @Validated UserEditForm userEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
    if(this.userService.isEmailChanged(userEditForm.getId(), userEditForm.getEmail()) && this.userService.isEmailRegistered(userEditForm.getEmail())) {
      FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "そのEメールアドレスは既に登録済みです。");
      bindingResult.addError(fieldError);
    }
    if(bindingResult.hasErrors()) {
      return "user/edit";
    }
    this.userService.update(userEditForm);
    redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");

    return "redirect:/user";
  }

}
