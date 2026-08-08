package com.andrew.mapper.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import com.andrew.dto.VisitRequest.VisitRequestCreateDTO;
import com.andrew.dto.VisitRequest.VisitRequestResponseDTO;
import com.andrew.model.User;
import com.andrew.model.VisitRequest;
import com.andrew.service.DeliveryService;

import jakarta.inject.Inject;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public abstract class VisitRequestMapper {
    @Inject
    protected DeliveryService deliveryService;
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "userId", qualifiedByName = "userFromId")
    @Mapping(target = "status", ignore = true)
    public abstract VisitRequest toEntity(VisitRequestCreateDTO dto);

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "deliveries", expression = "java(deliveryService.getDeliveryForVisit(entity.getId()))")
    public abstract VisitRequestResponseDTO toResponse(VisitRequest entity);

    @Named("userFromId")
    public User userFromId(Long id) {
        User user = new User();
        user.setId(id);
        return user;
    }
}
