package com.example.samuraitravel.samuraitravel.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.samuraitravel.samuraitravel.entity.Role;
import com.example.samuraitravel.samuraitravel.entity.User;
import com.example.samuraitravel.samuraitravel.form.AdminUserEditForm;
import com.example.samuraitravel.samuraitravel.form.SignupForm;
import com.example.samuraitravel.samuraitravel.form.UserEditForm;
import com.example.samuraitravel.samuraitravel.repository.RoleRepository;
import com.example.samuraitravel.samuraitravel.repository.UserRepository;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public User create(SignupForm signupForm) {
    User user = new User();
    Role role = this.roleRepository.findByName("ROLE_GENERAL");

    user.setRole(role);
    user.setName(signupForm.getName());
    user.setFurigana(signupForm.getFurigana());
    user.setPostalCode(signupForm.getPostalCode());
    user.setAddress(signupForm.getAddress());
    user.setPhoneNumber(signupForm.getPhoneNumber());
    user.setEmail(signupForm.getEmail());
    user.setPassword(this.passwordEncoder.encode(signupForm.getPassword()));
    user.setEnabled(false);

    return userRepository.save(user);
  }

  @Transactional
  public void update(UserEditForm userEditForm) {
    User user = this.userRepository.getReferenceById(userEditForm.getId());

    user.setName(userEditForm.getName());
    user.setFurigana(userEditForm.getFurigana());
    user.setPostalCode(userEditForm.getPostalCode());
    user.setAddress(userEditForm.getAddress());
    user.setPhoneNumber(userEditForm.getPhoneNumber());
    user.setEmail(userEditForm.getEmail());

    this.userRepository.save(user);
  }

  @Transactional
  public void adminUpdate(AdminUserEditForm adminUserEditForm) {
    User user = this.userRepository.getReferenceById(adminUserEditForm.getId());

    user.setName(adminUserEditForm.getName());
    user.setFurigana(adminUserEditForm.getFurigana());
    user.setPostalCode(adminUserEditForm.getPostalCode());
    user.setAddress(adminUserEditForm.getAddress());
    user.setPhoneNumber(adminUserEditForm.getPhoneNumber());
    user.setEmail(adminUserEditForm.getEmail());
    user.setPassword(this.passwordEncoder.encode(adminUserEditForm.getPassword()));
    user.setEnabled(adminUserEditForm.getEnabled());

    this.userRepository.save(user);
  }
  
  // Eメールアドレスが登録済みか確認
  public boolean isEmailRegistered(String email) {
    User user = userRepository.findByEmail(email);
    return user != null;
  }

  // 確認用パスワードが一致するかチェック
  public boolean isSamePassword(String password, String passwordConfirmation) {
    return password.equals(passwordConfirmation);
  }

  // ユーザーを有効にする。
  public void enableUser(User user) {
    user.setEnabled(true);
    this.userRepository.save(user);
  }

  // Eメールアドレスが変更されたかどうかチェック
  public boolean isEmailChanged(Integer id, String email) {
    User currentUser = this.userRepository.getReferenceById(id);

    return !email.equals(currentUser.getEmail());
  }

}
