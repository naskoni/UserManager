package com.naskoni.usermanager.service.impl;

import com.naskoni.usermanager.dto.UserResponseDto;
import com.naskoni.usermanager.entity.User;
import com.naskoni.usermanager.exception.DuplicateException;
import com.naskoni.usermanager.exception.NotFoundException;
import com.naskoni.usermanager.repository.UserArchiveRepository;
import com.naskoni.usermanager.repository.UserRepository;
import com.naskoni.usermanager.specification.SpecificationsBuilder;
import com.naskoni.usermanager.util.UsersCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.text.ParseException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private UserArchiveRepository userArchiveRepository;

  @InjectMocks private UserServiceImpl userService;

  @Test
  void createShouldSuccess() throws ParseException {
    var userRequestDto = UsersCreator.getUserRequestDto();
    var user = new User();
    userService.mapToEntity(userRequestDto, user);

    when(userRepository.save(any())).thenReturn(user);
    UserResponseDto userResponseDto = userService.create(userRequestDto);

    assertEquals(user.getId(), userResponseDto.getId());
    assertEquals(user.getFirstName(), userResponseDto.getFirstName());
    assertEquals(user.getLastName(), userResponseDto.getLastName());
    assertEquals(user.getEmailAddress(), userResponseDto.getEmailAddress());
    assertEquals(user.getDateOfBirth(), userResponseDto.getDateOfBirth());
  }

  @Test
  void createWithWithEmailInUseByOtherUserShouldThrowDuplicateException() throws ParseException {
    var userRequestDto = UsersCreator.getUserRequestDto();
    when(userRepository.findByEmailAddress(anyString())).thenReturn(Optional.of(new User()));
    assertThrows(DuplicateException.class, () -> userService.create(userRequestDto));
  }

  @Test
  void updateExistingUserShouldSuccess() throws ParseException {
    var userRequestDto = UsersCreator.getUserRequestDto();
    var user = new User();
    userService.mapToEntity(userRequestDto, user);

    when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    when(userRepository.save(any())).thenReturn(user);
    UserResponseDto userResponseDto = userService.update(1L, userRequestDto);

    assertEquals(user.getId(), userResponseDto.getId());
    assertEquals(user.getFirstName(), userResponseDto.getFirstName());
    assertEquals(user.getLastName(), userResponseDto.getLastName());
    assertEquals(user.getEmailAddress(), userResponseDto.getEmailAddress());
    assertEquals(user.getDateOfBirth(), userResponseDto.getDateOfBirth());
  }

  @Test
  void updateNonExistingUserShouldThrowNotFoundException() throws ParseException {
    var userRequestDto = UsersCreator.getUserRequestDto();
    assertThrows(NotFoundException.class, () -> userService.update(1L, userRequestDto));

    verify(userRepository, times(1)).findById(anyLong());
    verify(userRepository, times(0)).findByEmailAddress(any());
    verify(userRepository, times(0)).save(any());
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void updateExistingUserWithEmailInUseByOtherUserShouldThrowDuplicateException() {
    var userRequestDto = UsersCreator.getUserRequestDto();

    User userById = UsersCreator.getUser();
    userService.mapToEntity(userRequestDto, userById);

    User userByEmailAddress = UsersCreator.getUser();
    userService.mapToEntity(userRequestDto, userByEmailAddress);
    userByEmailAddress.setId(2L);

    when(userRepository.findById(anyLong())).thenReturn(Optional.of(userById));
    when(userRepository.findByEmailAddress(anyString()))
        .thenReturn(Optional.of(userByEmailAddress));

    assertThrows(DuplicateException.class, () -> userService.update(1L, userRequestDto));

    verify(userRepository, times(1)).findById(anyLong());
    verify(userRepository, times(1)).findByEmailAddress(any());
    verify(userRepository, times(0)).save(any());
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void deleteExistingUserShouldSuccess() {
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(UsersCreator.getUser()));
    userService.delete(1L);

    verify(userRepository, times(1)).findById(anyLong());
    verify(userRepository, times(1)).delete(any());
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void deleteNonExistingUserShouldThrowNotFoundException() {
    assertThrows(NotFoundException.class, () -> userService.delete(1L));

    verify(userRepository, times(1)).findById(anyLong());
    verify(userRepository, times(0)).delete(any());
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void findOneShouldSuccess() {
    var user = UsersCreator.getUser();

    when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    UserResponseDto userResponseDto = userService.findOne(1L);

    assertEquals(user.getId(), userResponseDto.getId());
    assertEquals(user.getFirstName(), userResponseDto.getFirstName());
    assertEquals(user.getLastName(), userResponseDto.getLastName());
    assertEquals(user.getEmailAddress(), userResponseDto.getEmailAddress());
    assertEquals(user.getDateOfBirth(), userResponseDto.getDateOfBirth());
  }

  @Test
  void findOneNonExistingUserShouldThrowNotFoundException() {
    assertThrows(NotFoundException.class, () -> userService.findOne(1L));
    verify(userRepository, times(1)).findById(anyLong());
    verifyNoMoreInteractions(userRepository);
  }

  @Test
  void findAllShouldSuccess() {
    List<User> users = UsersCreator.getUsers();
    Page<User> page = new PageImpl<>(users);

    Pageable pageable = Pageable.unpaged();
    SpecificationsBuilder<User> builder = new SpecificationsBuilder<>();
    Specification<User> spec = builder.build();
    Mockito.when(userRepository.findAll(spec, pageable)).thenReturn(page);
    Page<UserResponseDto> userResponseDtos = userService.findAll(null, pageable);
    assertEquals(10, userResponseDtos.getContent().size());

    UserResponseDto userResponseDto = userResponseDtos.iterator().next();
    User user = users.get(0);

    assertEquals(user.getId(), userResponseDto.getId());
    assertEquals(user.getFirstName(), userResponseDto.getFirstName());
    assertEquals(user.getLastName(), userResponseDto.getLastName());
    assertEquals(user.getEmailAddress(), userResponseDto.getEmailAddress());
    assertEquals(user.getDateOfBirth(), userResponseDto.getDateOfBirth());
  }
}
