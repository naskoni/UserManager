package com.naskoni.usermanager.service;

import com.naskoni.usermanager.dto.UserRequestDto;
import com.naskoni.usermanager.dto.UserResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

  UserResponseDto create(UserRequestDto user);

  UserResponseDto update(Long id, UserRequestDto user);

  void delete(Long id);

  UserResponseDto findOne(Long id);

  Page<UserResponseDto> findAll(String search, Pageable pageable);
}
