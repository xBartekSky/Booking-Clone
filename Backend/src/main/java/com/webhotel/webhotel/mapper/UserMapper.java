package com.webhotel.webhotel.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.webhotel.webhotel.dto.RegisterDto;
import com.webhotel.webhotel.dto.UserDto;
import com.webhotel.webhotel.dto.UserInfoDto;
import com.webhotel.webhotel.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "token", ignore = true)
    UserDto toUserDto(User user);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "phoneNumber", ignore = true)
    @Mapping(target = "surname", ignore = true)
    User registerToUser(RegisterDto registerDto);

    UserInfoDto userToUserInfoDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    User userInfoDtoToUser(UserInfoDto userInfoDto);
}