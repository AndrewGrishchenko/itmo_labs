package com.andrew.mapper.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import com.andrew.dto.ship.ShipCreateDTO;
import com.andrew.dto.ship.ShipResponseDTO;
import com.andrew.model.Ship;
import com.andrew.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface ShipMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "captain", source = "captainId", qualifiedByName = "userFromId")
    Ship toEntity(ShipCreateDTO dto);

    @Mapping(target = "captainId", source = "captain.id")
    ShipResponseDTO toResponse(Ship entity);

    @Named("userFromId")
    default User userFromId(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }
}
