package com.andrew.mapper.dto;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.andrew.dto.user.UserCreateResponseDTO;
import com.andrew.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface UserMapper {
    UserCreateResponseDTO toResponse(User entity);
}
