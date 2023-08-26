package com.example.samuraitravel.samuraitravel.form;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReviewEditForm {
  
  private Integer id;

  private Integer houseId;

  private Integer UserId;

  @NotNull
  private Integer score;

  @NotBlank(message = "コメントを入力してください。")
  @Length(min = 5, message = "コメントは最低5文字以上で入力してください。")
  @Length(max = 255, message = "入力できるコメントは最大255文字までです。")
  private String comment;

}
