package com.naskoni.usermanager.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class UserResponseDto {

  private Long id;

  private String firstName;

  private String lastName;

  private String emailAddress;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
  private Date dateOfBirth;
}
