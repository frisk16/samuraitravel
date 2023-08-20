package com.example.samuraitravel.samuraitravel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.samuraitravel.samuraitravel.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
  
  public Role findByName(String name);

}
