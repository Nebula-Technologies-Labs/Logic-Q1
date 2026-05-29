package com.logicq1.crm.modules.user.mapper;

import com.logicq1.crm.modules.auth.dto.RegisterRequest;
import com.logicq1.crm.modules.user.dto.UserDto;
import com.logicq1.crm.modules.user.dto.UserResponse;
import com.logicq1.crm.modules.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(RegisterRequest registerRequest);

    UserDto toUserDto(User user);

    @Mapping(target = "organizationId", source = "organization.id")
    @Mapping(target = "roleId", source = "role.id")
    @Mapping(target = "createdById", source = "createdBy.id")
    UserResponse toResponse(User user);
}