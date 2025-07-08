package com.myproject.expense_tracker.mapper;

import com.myproject.expense_tracker.dto.UserDto;
import com.myproject.expense_tracker.dto.UserResponseDto;
import com.myproject.expense_tracker.model.User;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto userDto);

    UserResponseDto toUserResponseDto(User user);

//    User toUser(UserResponseDto userResponseDto);

//    UserResponseDto toUserResponseDto(User user);


}
