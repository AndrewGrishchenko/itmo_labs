package com.andrew.service;

import java.util.List;

import com.andrew.dto.ship.ShipCreateDTO;
import com.andrew.dto.ship.ShipResponseDTO;
import com.andrew.exceptions.NotFoundException;
import com.andrew.mapper.dto.ShipMapper;
import com.andrew.model.Ship;
import com.andrew.repository.ShipRepository;
import com.andrew.security.CurrentUser;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;

@ApplicationScoped
public class ShipService {
    @Inject
    ShipRepository shipRepository;

    @Inject
    ShipMapper shipMapper;

    @Inject
    CurrentUser currentUser;

    @Transactional
    public List<ShipResponseDTO> getAll() {
        List<Ship> ships = switch (currentUser.getUser().getRole()) {
            case CAPTAIN -> shipRepository.getByCaptainId(currentUser.getUser().getId());
            case ADMIN, KEEPER -> shipRepository.getAll();
            default -> throw new ForbiddenException();
        };

        return ships.stream().map(shipMapper::toResponse).toList();
    }

    @Transactional
    public ShipResponseDTO createShip(ShipCreateDTO dto) {
        return createShip(shipMapper.toEntity(dto));
    }

    @Transactional
    private ShipResponseDTO createShip(Ship ship) {
        shipRepository.save(ship);
        return shipMapper.toResponse(ship);
    }

    @Transactional
    public ShipResponseDTO updateShip(Long id, ShipCreateDTO dto) {
        shipRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Ship", id));

        Ship toUpdate = shipMapper.toEntity(dto);
        toUpdate.setId(id);
        
        return shipMapper.toResponse(shipRepository.update(toUpdate));
    }

    @Transactional
    public void deleteShip(Long id) {
        Ship ship = shipRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Ship", id));
        
        shipRepository.delete(ship);
    }

    @Transactional
    public boolean isUserOwnsShip(Long shipId) {
        Ship ship = shipRepository.findById(shipId)
            .orElseThrow(() -> new NotFoundException("Ship", shipId));

        return ship.getCaptain().getId().equals(currentUser.getUser().getId());
    }
}
