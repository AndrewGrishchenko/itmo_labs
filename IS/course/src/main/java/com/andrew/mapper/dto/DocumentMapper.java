package com.andrew.mapper.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import com.andrew.dto.document.DocumentCreateDTO;
import com.andrew.dto.document.DocumentResponseDTO;
import com.andrew.model.Document;
import com.andrew.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface DocumentMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", source = "ownerId", qualifiedByName = "userFromId")
    Document toEntity(DocumentCreateDTO dto);

    @Mapping(target = "ownerId", source = "owner.id")
    DocumentResponseDTO toResponse(Document entity);

    @Named("userFromId")
    default User userFromId(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }
}
