package com.andrew.mapper.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;

import com.andrew.dto.delivery.DeliveryCreateDTO;
import com.andrew.dto.delivery.DeliveryResponseDTO;
import com.andrew.model.Delivery;
import com.andrew.model.Supply;
import com.andrew.model.VisitRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface DeliveryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "visitRequest", source = "visitRequestId", qualifiedByName = "visitRequestFromId")
    Delivery toEntity(DeliveryCreateDTO dto);

    @Mapping(target = "visitRequestId", source = "visitRequest.id")
    DeliveryResponseDTO toResponse(Delivery entity);

    @Named("supplyFromId")
    default Supply supplyFromId(Long id) {
        Supply supply = new Supply();
        supply.setId(id);
        return supply;
    }

    @Named("visitRequestFromId")
    default VisitRequest visitRequestFromId(Long id) {
        VisitRequest visitRequest = new VisitRequest();
        visitRequest.setId(id);
        return visitRequest;
    }
}
