package com.naskoni.usermanager.dto;

import lombok.Data;

@Data
public class ApiErrorDto {

  private int status;
  private String message;
  private String rootCauseMessage;
}
