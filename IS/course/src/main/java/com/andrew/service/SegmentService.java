package com.andrew.service;

import java.util.Comparator;
import java.util.List;

import com.andrew.mapper.dto.SegmentMapper;
import com.andrew.model.Segment;
import com.andrew.repository.SegmentRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class SegmentService {
    @Inject
    SegmentRepository segmentRepository;

    //TODO: about to remove
    @Inject
    SegmentMapper segmentMapper;

    @Transactional
    public List<Long> getSegmentsForRoute(Long routeId) {
        List<Segment> segments = segmentRepository.getByRouteId(routeId);

        return segments.stream()
            .sorted(Comparator.comparingLong(Segment::getStepIndex))
            .map(segment -> segment.getZone().getId())
            .toList();        
    }

    // @Transactional
    // public SegmentResponseDTO createSegment(SegmentCreateDTO dto) {
    //     return createSegment(segmentMapper.toEntity(dto));
    // }

    // @Transactional
    // private SegmentResponseDTO createSegment(Segment segment) {
    //     segmentRepository.save(segment);
    //     return segmentMapper.toResponse(segment);
    // }

    // @Transactional
    // public SegmentResponseDTO updateShip(Long id, SegmentCreateDTO dto) {
    //     segmentRepository.findById(id)
    //         .orElseThrow(() -> new NotFoundException("Segment", id));
        
    //     Segment toUpdate = segmentMapper.toEntity(dto);
    //     toUpdate.setId(id);
        
    //     return segmentMapper.toResponse(segmentRepository.update(toUpdate));
    // }

    // @Transactional
    // public void deleteSegment(Long id) {
    //     Segment segment = segmentRepository.findById(id)
    //         .orElseThrow(() -> new NotFoundException("Segment", id));
        
    //     segmentRepository.delete(segment);
    // }
}
