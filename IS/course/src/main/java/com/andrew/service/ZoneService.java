package com.andrew.service;

import java.util.List;

import com.andrew.dto.zone.ZoneCreateDTO;
import com.andrew.dto.zone.ZoneResponseDTO;
import com.andrew.exceptions.NotFoundException;
import com.andrew.mapper.dto.ZoneMapper;
import com.andrew.model.Zone;
import com.andrew.repository.ZoneRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class ZoneService {
    @Inject
    ZoneRepository zoneRepository;

    @Inject
    ZoneMapper zoneMapper;

    @Transactional
    public List<ZoneResponseDTO> getAll() {
        return zoneRepository.getAll().stream().map(zoneMapper::toResponse).toList();
    }

    @Transactional
    public ZoneResponseDTO createZone(ZoneCreateDTO dto) {
        return createZone(zoneMapper.toEntity(dto));
    }

    @Transactional
    private ZoneResponseDTO createZone(Zone zone) {
        zoneRepository.save(zone);
        return zoneMapper.toResponse(zone);
    }

    @Transactional
    public ZoneResponseDTO updateZone(Long id, ZoneCreateDTO dto) {
        zoneRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Zone", id));
        
        Zone toUpdate = zoneMapper.toEntity(dto);
        toUpdate.setId(id);

        return zoneMapper.toResponse(zoneRepository.update(toUpdate));
    }

    @Transactional
    public void deletePerson(Long id) {
        Zone zone = zoneRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Zone", id));

        zoneRepository.delete(zone);
    }
}
