package com.naskoni.usermanager.controller;

import com.naskoni.usermanager.dto.UserRequestDto;
import com.naskoni.usermanager.dto.UserResponseDto;
import com.naskoni.usermanager.service.UserService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.text.ParseException;

@Api(tags = "Users")
@Slf4j
@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

  @Autowired private UserService userService;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @ApiOperation(value = "Create new user", response = UserRequestDto.class)
  public UserResponseDto create(
      @Validated @RequestBody @ApiParam(value = "User object") UserRequestDto userRequestDto)
      throws ParseException {
    log.info("Create user request: " + userRequestDto.toString());
    UserResponseDto savedUserDto = userService.create(userRequestDto);
    log.info("Created user response: " + savedUserDto.toString());
    return savedUserDto;
  }

  @PutMapping("/{id}")
  @ApiOperation(value = "Update existing user", response = UserRequestDto.class)
  public UserResponseDto update(
      @PathVariable @ApiParam(value = "The id of the user for update") Long id,
      @Validated @RequestBody @ApiParam(value = "User object") UserRequestDto userRequestDto) {
    log.info("Update user request: " + userRequestDto.toString());
    UserResponseDto savedUserDto = userService.update(id, userRequestDto);
    log.info("Updated user response: " + savedUserDto.toString());
    return savedUserDto;
  }

  @DeleteMapping("/{id}")
  @ApiOperation(value = "Delete existing user")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable @ApiParam(value = "The id of the user for delete") Long id) {
    userService.delete(id);
  }

  @GetMapping("/{id}")
  @ApiOperation(value = "Find user by id")
  public UserResponseDto findOne(
      @PathVariable @ApiParam(value = "The id of the user to retrieve") Long id) {
    return userService.findOne(id);
  }

  @GetMapping
  @ApiOperation(
      value = "Find all users",
      notes = "Retrieves a list of all users. Supports paging and sorting (optional).",
      responseContainer = "List",
      response = UserResponseDto.class)
  @ApiImplicitParams({
    @ApiImplicitParam(
        name = "page",
        dataType = "int",
        paramType = "query",
        value = "The number of the results page you want to retrieve (0..N)."),
    @ApiImplicitParam(
        name = "size",
        dataType = "int",
        paramType = "query",
        value = "Number of records per page."),
    @ApiImplicitParam(
        name = "sort",
        allowMultiple = true,
        dataType = "string",
        paramType = "query",
        value =
            "Sorting criteria in the format: property(,asc|desc). "
                + "Default sort order is ascending. "
                + "Multiple sort criteria are supported.")
  })
  public Page<UserResponseDto> findAll(
      @ApiParam(
              name = "search",
              value = "Search query by User property, supported operations are >, <, :",
              example = "firstName:Joe")
          @RequestParam(value = "search", required = false)
          String search,
      @ApiIgnore Pageable pageable) {
    return userService.findAll(search, pageable);
  }
}
