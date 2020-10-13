package com.naskoni.usermanager.util;

import com.naskoni.usermanager.dto.UserRequestDto;
import com.naskoni.usermanager.dto.UserResponseDto;
import com.naskoni.usermanager.entity.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class UsersCreator {

  private static Date date;

  static {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
      sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
      date = sdf.parse("02-02-2002");
    } catch (ParseException e) {
      // do nothing
    }
  }

  public static User getUser() {
    var user = new User();
    user.setId(1L);
    user.setFirstName("first name");
    user.setLastName("last name");
    user.setEmailAddress("somemail@example.com");
    user.setDateOfBirth(date);

    return user;
  }

  public static List<User> getUsers() {
    List<User> users = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      users.add(getUser());
    }

    return users;
  }

  public static UserRequestDto getUserRequestDto() {
    var user = new UserRequestDto();
    user.setFirstName("first name");
    user.setLastName("last name");
    user.setEmailAddress("somemail@example.com");
    user.setDateOfBirth("02-02-2002");
    return user;
  }

  public static UserResponseDto getUserResponseDto() {
    var user = new UserResponseDto();
    user.setId(1L);
    user.setFirstName("first name");
    user.setLastName("last name");
    user.setEmailAddress("somemail@example.com");
    user.setDateOfBirth(date);

    return user;
  }

  public static List<UserResponseDto> getUserResponseDtos() {
    List<UserResponseDto> users = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      users.add(getUserResponseDto());
    }

    return users;
  }
}
