package com.naskoni.usermanager.service.impl;

import com.naskoni.usermanager.dto.UserRequestDto;
import com.naskoni.usermanager.dto.UserResponseDto;
import com.naskoni.usermanager.entity.User;
import com.naskoni.usermanager.entity.UserArchive;
import com.naskoni.usermanager.exception.DuplicateException;
import com.naskoni.usermanager.exception.InvalidDateException;
import com.naskoni.usermanager.exception.NotFoundException;
import com.naskoni.usermanager.repository.UserArchiveRepository;
import com.naskoni.usermanager.repository.UserRepository;
import com.naskoni.usermanager.service.UserService;
import com.naskoni.usermanager.specification.SpecificationsBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.regex.Matcher;

@Service
public class UserServiceImpl implements UserService {

  private static final String USER_NOT_FOUND = "User with id: %d could not be found";
  private static final String INVALID_DATE =
      "The provided dateOfBirth is invalid or not in expected format, please use valid date in format dd-MM-yyyy";
  private static final String USER_EMAIL_EXIST = "User with email address: %s already exists";
  private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");

  @Autowired private UserRepository userRepository;
  @Autowired private UserArchiveRepository userArchiveRepository;

  @Transactional
  @Override
  public UserResponseDto create(UserRequestDto userRequestDto) {
    Optional<User> optionalUser =
        userRepository.findByEmailAddress(userRequestDto.getEmailAddress());
    if (optionalUser.isPresent()) {
      throw new DuplicateException(
          String.format(USER_EMAIL_EXIST, userRequestDto.getEmailAddress()));
    }

    var user = new User();
    mapToEntity(userRequestDto, user);

    User savedUser = userRepository.save(user);
    return mapToDto(savedUser);
  }

  @Transactional
  @Override
  public UserResponseDto update(Long id, UserRequestDto userRequestDto) {
    Optional<User> optionalUser = userRepository.findById(id);
    if (optionalUser.isPresent()) {
      User user = optionalUser.get();
      Optional<User> optionalbyEmailAddress =
          userRepository.findByEmailAddress(userRequestDto.getEmailAddress());
      if (optionalbyEmailAddress.isPresent() && !optionalbyEmailAddress.get().getId().equals(id)) {
        throw new DuplicateException(
            String.format(USER_EMAIL_EXIST, userRequestDto.getEmailAddress()));
      }

      mapToEntity(userRequestDto, user);

      User savedUser = userRepository.save(user);
      return mapToDto(savedUser);
    } else {
      throw new NotFoundException(String.format(USER_NOT_FOUND, id));
    }
  }

  @Transactional
  @Override
  public void delete(Long id) {
    Optional<User> userOptional = userRepository.findById(id);
    if (userOptional.isPresent()) {
      User user = userOptional.get();
      UserArchive userArchive = new UserArchive();
      BeanUtils.copyProperties(user, userArchive);
      userArchiveRepository.save(userArchive);

      userRepository.delete(user);
    } else {
      throw new NotFoundException(String.format(USER_NOT_FOUND, id));
    }
  }

  @Override
  public UserResponseDto findOne(Long id) {
    Optional<User> optionalUser = userRepository.findById(id);
    if (optionalUser.isPresent()) {
      return mapToDto(optionalUser.get());
    } else {
      throw new NotFoundException(String.format(USER_NOT_FOUND, id));
    }
  }

  @Override
  public Page<UserResponseDto> findAll(String search, Pageable pageable) {
    SpecificationsBuilder<User> builder = new SpecificationsBuilder<>();
    Matcher matcher = Helper.getMatcher(search);
    while (matcher.find()) {
      builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
    }

    Specification<User> spec = builder.build();
    Page<User> users = userRepository.findAll(spec, pageable);

    return users.map(this::mapToDto);
  }

  private UserResponseDto mapToDto(User user) {
    var userDto = new UserResponseDto();
    BeanUtils.copyProperties(user, userDto);

    return userDto;
  }

  void mapToEntity(UserRequestDto userRequestDto, User user) {
    BeanUtils.copyProperties(userRequestDto, user);
    try {
      user.setDateOfBirth(simpleDateFormat.parse(userRequestDto.getDateOfBirth()));
    } catch (ParseException e) {
      throw new InvalidDateException(String.format(INVALID_DATE, userRequestDto.getDateOfBirth()));
    }
  }
}
