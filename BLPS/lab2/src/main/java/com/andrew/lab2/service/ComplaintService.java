package com.andrew.lab2.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andrew.lab2.dto.complaint.ComplaintCreateRequest;
import com.andrew.lab2.dto.complaint.ComplaintResponse;
import com.andrew.lab2.dto.moderator.ModeratorDecisionRequest;
import com.andrew.lab2.entity.enums.ComplaintStatus;
import com.andrew.lab2.entity.enums.VideoStatus;
import com.andrew.lab2.entity.studs.Complaint;
import com.andrew.lab2.entity.studs2.Video;
import com.andrew.lab2.entity.xml.XmlUserDetails;
import com.andrew.lab2.exception.ForbiddenException;
import com.andrew.lab2.exception.NotFoundException;
import com.andrew.lab2.exception.ValidationException;
import com.andrew.lab2.repository.studs.ComplaintRepository;
import com.andrew.lab2.repository.studs.MusicRepository;
import com.andrew.lab2.repository.studs2.VideoRepository;
import com.andrew.lab2.util.ResponseMapper;
import com.andrew.lab2.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComplaintService {
    private final ComplaintRepository complaintRepository;
    private final VideoRepository videoRepository;
    private final MusicRepository musicRepository;

    @Transactional
    public ComplaintResponse createComplaint(ComplaintCreateRequest request) {
        XmlUserDetails rightsHolder = SecurityUtils.getCurrentPrincipal();
        
        Video video = videoRepository.findById(request.videoId())
            .orElseThrow(() -> new NotFoundException("Video", request.videoId()));

        Complaint complaint = new Complaint();
        complaint.setRightsholderId(rightsHolder.getId());
        complaint.setVideoId(request.videoId());
        complaint.setVideoAuthorId(video.getAuthorId());
        complaint.setClaimDetails(request.claimDetails());
        complaint.setStatus(ComplaintStatus.RECEIVED);

        return ResponseMapper.toResponse(complaintRepository.save(complaint));
    }

    @Transactional(readOnly = true)
    public Page<ComplaintResponse> getAll(Pageable pageable) {
        XmlUserDetails user = SecurityUtils.getCurrentPrincipal();

        Page<Complaint> page = switch (user.getRole()) {
            case MODERATOR ->
                complaintRepository.findByStatus(
                    ComplaintStatus.PENDING_MODERATOR,
                    pageable
                );

            case AUTHOR ->
                complaintRepository.findByVideoAuthorId(
                    user.getId(),
                    pageable
                );

            case RIGHTSHOLDER ->
                complaintRepository.findByRightsholderId(
                    user.getId(),
                    pageable
                );
        };

        return page.map(ResponseMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ComplaintResponse getById(Long id) {
        Complaint complaint = complaintRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Complaint", id));

        if (!hasAccess(SecurityUtils.getCurrentPrincipal(), complaint))
            throw new ForbiddenException();

        return ResponseMapper.toResponse(complaint);
    }

    @Transactional
    public ComplaintResponse updateComplaint(Long id, ComplaintCreateRequest request) {
        Complaint existing = complaintRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Complaint", id));

        if (!existing.getRightsholderId().equals(SecurityUtils.getCurrentUserId()))
            throw new ForbiddenException();

        if (!existing.getStatus().equals(ComplaintStatus.RECEIVED))
            throw new ValidationException("Unable to modify sent complaint");

        if (!existing.getVideoId().equals(request.videoId())) {
            videoRepository.findById(request.videoId())
                .orElseThrow(() -> new NotFoundException("Video", id));

            existing.setVideoId(request.videoId());
        }

        existing.setClaimDetails(request.claimDetails());

        return ResponseMapper.toResponse(complaintRepository.save(existing));
    }

    @Transactional
    public void deleteComplaint(Long id) {
        Complaint existing = complaintRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Complaint", id));

        if (!existing.getRightsholderId().equals(SecurityUtils.getCurrentUserId()))
            throw new ForbiddenException();

        if (!existing.getStatus().equals(ComplaintStatus.RECEIVED))
            throw new ValidationException("Unable to modify sent complaint");

        complaintRepository.delete(existing);
    }

    @Transactional
    public ComplaintResponse submitComplaint(Long id) {
        Complaint complaint = complaintRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Complaint", id));

        if (violatesCopyright(complaint)) {
            complaint.setStatus(ComplaintStatus.ACCEPTED_BY_AUTO);
            
            Video toBlock = videoRepository.findById(complaint.getVideoId())
                .orElseThrow(() -> new NotFoundException("Video", id));

            toBlock.setStatus(VideoStatus.BLOCKED_BY_COPYRIGHT);
            videoRepository.save(toBlock);
        } else {
            complaint.setStatus(ComplaintStatus.PENDING_MODERATOR);
        }

        return ResponseMapper.toResponse(complaintRepository.save(complaint));
    }

    private boolean hasAccess(XmlUserDetails user, Complaint complaint) {
        return switch (user.getRole()) {
            case RIGHTSHOLDER ->
                complaint.getRightsholderId().equals(user.getId());
            
            case AUTHOR ->
                videoRepository.findById(complaint.getVideoId())
                    .orElseThrow(() -> new NotFoundException("Video", complaint.getVideoId()))
                    .getAuthorId().equals(user.getId());

            case MODERATOR ->
                complaint.getStatus().equals(ComplaintStatus.PENDING_MODERATOR);
        };
    }

    @Transactional
    private boolean violatesCopyright(Complaint complaint) {
        List<String> presence = videoRepository.findById(complaint.getVideoId())
                .orElseThrow(() -> new NotFoundException("Video", complaint.getVideoId()))
                .getMusic();
        
        if (presence == null)
            return false;
        
        Set<String> holded = new HashSet<>(
            musicRepository.findNamesByRightsholderId(SecurityUtils.getCurrentUserId())
        );

        return presence.stream().anyMatch(holded::contains);
    }

    @Transactional
    public void acceptViolation(Long complaintId, ModeratorDecisionRequest request) {
        // transactionRunner.execute(() -> {
            Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new NotFoundException("Complaint", complaintId));

            Video video = videoRepository.findById(complaint.getVideoId())
                .orElseThrow(() -> new NotFoundException("Video", complaint.getVideoId()));

            complaint.setStatus(ComplaintStatus.ACCEPTED_BY_MODERATOR);
            complaint.setModeratorComment(request.getModeratorComment());
            video.setStatus(VideoStatus.BLOCKED_BY_COPYRIGHT);

            complaintRepository.save(complaint);
            videoRepository.save(video);

            // throw new RuntimeException("test rollback");

            // return null;
        // });
    }

    @Transactional
    public void rejectViolation(Long complaintId, ModeratorDecisionRequest request) {
        Complaint complaint = complaintRepository.findById(complaintId)
            .orElseThrow(() -> new NotFoundException("Complaint", complaintId));

        complaint.setStatus(ComplaintStatus.REJECTED_BY_MODERATOR);
        complaint.setModeratorComment(request.getModeratorComment());

        complaintRepository.save(complaint);
    }

    // @Transactional
    // private XmlUser getCurrentUser() {
    //     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    //     return userRepository.findByUsername(auth.getName())
    //         .orElseThrow(() -> new NotFoundException("User with username " + auth.getName() + " not found"));
    // }
}
