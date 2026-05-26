package com.logicq1.crm.modules.user.mapper;

import com.logicq1.crm.modules.auth.dto.RegisterRequest;
import com.logicq1.crm.modules.user.dto.UserDto;
import com.logicq1.crm.modules.user.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(RegisterRequest registerRequest);

    UserDto toUserDto(User user);
}
