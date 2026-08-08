package com.andrew.service;

import java.util.List;
import java.util.Optional;

import com.andrew.dto.supply.SupplyEditDTO;
import com.andrew.dto.supply.SupplyResponseDTO;
import com.andrew.mapper.dto.SupplyMapper;
import com.andrew.model.Supply;
import com.andrew.repository.SupplyRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class SupplyService {
    @Inject
    SupplyRepository supplyRepository;

    @Inject
    SupplyMapper supplyMapper;

    @Transactional
    public List<SupplyResponseDTO> getAll() {
        return supplyRepository.getAll().stream()
            .map(supplyMapper::toResponse)
            .toList();
    }

    @Transactional
    public SupplyResponseDTO editSupply(SupplyEditDTO dto) {
        Optional<Supply> supplyOpt = supplyRepository.findByType(dto.name());

        if (supplyOpt.isPresent()) {
            Supply supply = supplyOpt.get();
            supply.setAmount(dto.amount());
            return supplyMapper.toResponse(supplyRepository.update(supply));
        } else {
            Supply supply = new Supply(dto.name(), dto.amount());
            supplyRepository.save(supply);
            return supplyMapper.toResponse(supply);
        }
    }
}
