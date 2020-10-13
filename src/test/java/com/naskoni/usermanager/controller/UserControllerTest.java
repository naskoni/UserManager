package com.naskoni.usermanager.controller;

import com.google.gson.Gson;
import com.naskoni.usermanager.dto.UserRequestDto;
import com.naskoni.usermanager.exception.DuplicateException;
import com.naskoni.usermanager.exception.NotFoundException;
import com.naskoni.usermanager.service.UserService;
import com.naskoni.usermanager.util.UsersCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

  private static final String USERS_URI = "/users";
  private static final String USERS_URI_WITH_PARAM = "/users/1";
  private static final String RESPONSE =
      "{\"id\":1,\"firstName\":\"first name\",\"lastName\":\"last name\",\"emailAddress\":\"somemail@example.com\",\"dateOfBirth\":\"02-02-2002\"}";

  private MockMvc mockMvc;

  @InjectMocks private UserController userController;

  @Mock private UserService userService;

  private Gson gson = new Gson();

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(userController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .setControllerAdvice(new ErrorHandler())
            .build();
  }

  @Test
  void createWithValidDtoShouldReturnHttpCreated() throws Exception {
    String json = gson.toJson(UsersCreator.getUserRequestDto());

    when(userService.create(any())).thenReturn(UsersCreator.getUserResponseDto());

    MvcResult mvcResult =
        mockMvc
            .perform(
                post(USERS_URI)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andReturn();
    String content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    assertEquals(RESPONSE, content);
    verify(userService, times(1)).create(any());
    verifyNoMoreInteractions(userService);
  }

  @Test
  void createWithInvalidDtoShouldFailHttpBadRequest() throws Exception {
    String json = gson.toJson(new UserRequestDto());

    MvcResult mvcResult =
        mockMvc
            .perform(
                post(USERS_URI)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();
    mvcResult.getResolvedException().getClass().equals(MethodArgumentNotValidException.class);
    verify(userService, times(0)).create(any());
    verifyNoMoreInteractions(userService);
  }

  @Test
  void createWithExistingEmailShouldFailHttpConflict() throws Exception {
    doThrow(new DuplicateException("")).when(userService).create(any());
    String json = gson.toJson(UsersCreator.getUserRequestDto());

    MvcResult mvcResult =
        mockMvc
            .perform(
                post(USERS_URI)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isConflict())
            .andReturn();
    mvcResult.getResolvedException().getClass().equals(DuplicateException.class);
    verify(userService, times(1)).create(any());
    verifyNoMoreInteractions(userService);
  }

  @Test
  void updateWithValidDtoShouldReturnHttpOk() throws Exception {
    String json = gson.toJson(UsersCreator.getUserRequestDto());

    when(userService.update(anyLong(), any())).thenReturn(UsersCreator.getUserResponseDto());

    MvcResult mvcResult =
        mockMvc
            .perform(
                put(USERS_URI_WITH_PARAM)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    assertEquals(RESPONSE, content);
    verify(userService, times(1)).update(anyLong(), any());
    verifyNoMoreInteractions(userService);
  }

  @Test
  void updateWithInvalidDtoShouldFailHttpBadRequest() throws Exception {
    String json = gson.toJson(new UserRequestDto());

    MvcResult mvcResult =
        mockMvc
            .perform(
                put(USERS_URI_WITH_PARAM)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andReturn();
    assertEquals(
        MethodArgumentNotValidException.class, mvcResult.getResolvedException().getClass());
    verify(userService, times(0)).update(any(), any());
    verifyNoMoreInteractions(userService);
  }

  @Test
  void updateNonExistingUserShouldFailHttpNotFound() throws Exception {
    String json = gson.toJson(UsersCreator.getUserRequestDto());

    doThrow(new NotFoundException("")).when(userService).update(anyLong(), any());

    MvcResult mvcResult =
        mockMvc
            .perform(
                put(USERS_URI_WITH_PARAM)
                    .content(json)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn();
    assertEquals(NotFoundException.class, mvcResult.getResolvedException().getClass());
    verify(userService, times(1)).update(anyLong(), any());
    verifyNoMoreInteractions(userService);
  }

  @Test
  void deleteExistingUserShouldReturnHttpNoContent() throws Exception {
    mockMvc.perform(delete(USERS_URI_WITH_PARAM)).andExpect(status().isNoContent());
  }

  @Test
  void deleteNonExistingUserShouldFailHttpNotFound() throws Exception {
    doThrow(new NotFoundException("")).when(userService).delete(anyLong());

    MvcResult mvcResult =
        mockMvc.perform(delete(USERS_URI_WITH_PARAM)).andExpect(status().isNotFound()).andReturn();

    assertEquals(NotFoundException.class, mvcResult.getResolvedException().getClass());
    verify(userService, times(1)).delete(anyLong());
    verifyNoMoreInteractions(userService);
  }

  @Test
  void findOneShouldReturnHttpOk() throws Exception {
    when(userService.findOne(anyLong())).thenReturn(UsersCreator.getUserResponseDto());

    MvcResult mvcResult =
        mockMvc
            .perform(get(USERS_URI_WITH_PARAM).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    assertEquals(RESPONSE, content);
    verify(userService, times(1)).findOne(anyLong());
    verifyNoMoreInteractions(userService);
  }

  @Test
  void findAllShouldReturnHttpOk() throws Exception {
    when(userService.findAll(any(), any()))
        .thenReturn(new PageImpl<>(UsersCreator.getUserResponseDtos()));

    MvcResult mvcResult =
        mockMvc
            .perform(get(USERS_URI).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn();
    String content = mvcResult.getResponse().getContentAsString();
    assertNotNull(content);
    assertTrue(content.contains(RESPONSE));
    assertTrue(content.contains("\"totalElements\":10"));
    verify(userService, times(1)).findAll(any(), any());
    verifyNoMoreInteractions(userService);
  }
}
