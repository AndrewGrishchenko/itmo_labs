package com.andrew.service;

import java.util.List;

import com.andrew.dto.delivery.DeliveryCreateDTO;
import com.andrew.dto.delivery.DeliveryResponseDTO;
import com.andrew.exceptions.NotFoundException;
import com.andrew.mapper.dto.DeliveryMapper;
import com.andrew.model.Delivery;
import com.andrew.model.VisitRequest;
import com.andrew.model.enums.VisitRequestStatus;
import com.andrew.repository.DeliveryRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;

@ApplicationScoped
public class DeliveryService {
    @Inject
    DeliveryRepository deliveryRepository;

    @Inject
    DeliveryMapper deliveryMapper;

    @Inject
    VisitRequestService visitRequestService;

    @Transactional
    public Delivery getById(Long id) {
        return deliveryRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Delivery", id));
    }

    @Transactional
    public List<DeliveryResponseDTO> getDeliveryForVisit(Long visitId) {
        return deliveryRepository.findByVisitId(visitId).stream()
            .map(deliveryMapper::toResponse)
            .toList();
    }

    @Transactional
    public DeliveryResponseDTO createDelivery(DeliveryCreateDTO dto) {
        VisitRequest visitRequest = visitRequestService.getById(dto.visitRequestId());
        if (!visitRequest.getStatus().equals(VisitRequestStatus.DRAFT))
            throw new ForbiddenException();
        
        return createDelivery(deliveryMapper.toEntity(dto));
    }

    @Transactional
    private DeliveryResponseDTO createDelivery(Delivery delivery) {
        deliveryRepository.save(delivery);
        return deliveryMapper.toResponse(delivery);
    }

    @Transactional
    public DeliveryResponseDTO updateDelivery(Long id, DeliveryCreateDTO dto) {
        Delivery existing = getById(id);
        if (!existing.getVisitRequest().getStatus().equals(VisitRequestStatus.DRAFT))
            throw new ForbiddenException();

        Delivery toUpdate = deliveryMapper.toEntity(dto);
        toUpdate.setId(id);
        if (!toUpdate.getVisitRequest().getId().equals(existing.getVisitRequest().getId()))
            throw new ForbiddenException();

        return deliveryMapper.toResponse(deliveryRepository.update(toUpdate));
    }

    @Transactional
    public void deleteDelivery(Long id) {
        Delivery delivery = getById(id);
        if (!delivery.getVisitRequest().getStatus().equals(VisitRequestStatus.DRAFT))
            throw new ForbiddenException();

        deliveryRepository.delete(delivery);
    }
}
