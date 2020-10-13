package com.naskoni.usermanager.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class UserRequestDto {

  @NotBlank
  @Size(min = 1, max = 50)
  private String firstName;

  @NotBlank
  @Size(min = 1, max = 50)
  private String lastName;

  @Email private String emailAddress;

  private String dateOfBirth;
}
