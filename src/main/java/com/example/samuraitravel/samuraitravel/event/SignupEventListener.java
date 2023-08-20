package com.example.samuraitravel.samuraitravel.event;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.samuraitravel.samuraitravel.entity.User;
import com.example.samuraitravel.samuraitravel.service.VerificationTokenService;

@Component
public class SignupEventListener {
  private final VerificationTokenService verificationTokenService;
  private final JavaMailSender javaMailSender;

  public SignupEventListener(VerificationTokenService verificationTokenService, JavaMailSender javaMailSender) {
    this.verificationTokenService = verificationTokenService;
    this.javaMailSender = javaMailSender;
  }

  @EventListener
  public void onSignupEvent(SignupEvent signupEvent) {
    User user = signupEvent.getUser();
    String requestUrl = signupEvent.getRequestUrl();
    String token = UUID.randomUUID().toString();
    this.verificationTokenService.create(user, token);

    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    simpleMailMessage.setTo(user.getEmail());
    simpleMailMessage.setSubject("メール認証");
    simpleMailMessage.setText(
      "以下のリンクをクリックして会員登録を完了させてください。" +
      "\n" +
      requestUrl +
      "/verify?token=" +
      token
    );
    this.javaMailSender.send(simpleMailMessage);
  }
}
