package com.example.samuraitravel.samuraitravel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.samuraitravel.entity.User;
import com.example.samuraitravel.samuraitravel.entity.VerificationToken;
import com.example.samuraitravel.samuraitravel.event.SignupEventPublisher;
import com.example.samuraitravel.samuraitravel.form.SignupForm;
import com.example.samuraitravel.samuraitravel.service.UserService;
import com.example.samuraitravel.samuraitravel.service.VerificationTokenService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class AuthController {

  private final UserService userService;
  private final VerificationTokenService verificationTokenService;
  private final SignupEventPublisher signupEventPublisher;

  public AuthController(UserService userService, VerificationTokenService verificationTokenService, SignupEventPublisher signupEventPublisher) {
    this.userService = userService;
    this.verificationTokenService = verificationTokenService;
    this.signupEventPublisher = signupEventPublisher;
  }

  @GetMapping("/login")
  public String login() {
    return "auth/login";
  }

  @GetMapping("/signup")
  public String signup(Model model) {
    model.addAttribute("signupForm", new SignupForm());

    return "auth/signup";
  }

  @PostMapping("/signup")
  public String signup(@ModelAttribute @Validated SignupForm signupForm, BindingResult bindingResult, RedirectAttributes redirectAttributes, HttpServletRequest httpServletRequest) {
    if(this.userService.isEmailRegistered(signupForm.getEmail())) {
      FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "そのEメールアドレスは既に利用されています。");
      bindingResult.addError(fieldError);
    }

    if(!this.userService.isSamePassword(signupForm.getPassword(), signupForm.getPasswordConfirmation())) {
      FieldError fieldError = new FieldError(bindingResult.getObjectName(), "password", "確認用パスワードが一致しません。");
      bindingResult.addError(fieldError);
    }

    if(bindingResult.hasErrors()) {
      return "auth/signup";
    }

    User user = this.userService.create(signupForm);
    String requestUrl = new String(httpServletRequest.getRequestURL());
    this.signupEventPublisher.publishSignupEvent(user, requestUrl);
    redirectAttributes.addFlashAttribute("successMessage", "ご入力頂いたEメールアドレスに認証メールを送信しました、ご確認ください。");

    return "redirect:/";
  }

  @GetMapping("/signup/verify")
  public String verify(@RequestParam(name = "token") String token, Model model) {
    VerificationToken verificationToken = this.verificationTokenService.getVerificationToken(token);

    if(verificationToken != null) {
      User user = verificationToken.getUser();
      this.userService.enableUser(user);
      model.addAttribute("successMessage", "会員登録が完了しました。");
    } else {
      model.addAttribute("errorMessage", "トークンが無効です。");
    }

    return "auth/verify";
  }
  
}
