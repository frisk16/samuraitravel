package com.example.samuraitravel.samuraitravel.form;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FavoriteRegisterForm {
  
  @NotNull
  private Integer userId;

  @NotNull
  private Integer houseId;

}
