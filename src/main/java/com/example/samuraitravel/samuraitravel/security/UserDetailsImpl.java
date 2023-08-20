package com.example.samuraitravel.samuraitravel.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.samuraitravel.samuraitravel.entity.User;

public class UserDetailsImpl implements UserDetails {
  private final User user; 
  private final Collection<GrantedAuthority> authorities;

  public UserDetailsImpl(User user, Collection<GrantedAuthority> authorities) {
    this.user = user;
    this.authorities = authorities;
  }

  public User getUser() {
    return this.user;
  }

  @Override
  public String getPassword() {
    return this.user.getPassword();
  }

  @Override
  public String getUsername() {
    return this.user.getEmail();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.authorities;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return this.user.getEnabled();
  }

}