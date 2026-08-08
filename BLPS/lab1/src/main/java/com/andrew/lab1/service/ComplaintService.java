package com.andrew.lab1.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andrew.lab1.dto.complaint.ComplaintCreateRequest;
import com.andrew.lab1.dto.complaint.ComplaintResponse;
import com.andrew.lab1.dto.moderator.ModeratorDecisionRequest;
import com.andrew.lab1.entity.Complaint;
import com.andrew.lab1.entity.User;
import com.andrew.lab1.entity.Video;
import com.andrew.lab1.entity.enums.ComplaintStatus;
import com.andrew.lab1.entity.enums.VideoStatus;
import com.andrew.lab1.exception.ForbiddenException;
import com.andrew.lab1.exception.NotFoundException;
import com.andrew.lab1.exception.ValidationException;
import com.andrew.lab1.repository.ComplaintRepository;
import com.andrew.lab1.repository.MusicRepository;
import com.andrew.lab1.repository.UserRepository;
import com.andrew.lab1.repository.VideoRepository;
import com.andrew.lab1.util.ResponseMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComplaintService {
    private final ComplaintRepository complaintRepository;
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    private final MusicRepository musicRepository;

    @Transactional
    public ComplaintResponse createComplaint(ComplaintCreateRequest request) {
        User rightsHolder = getCurrentUser();
        
        Video video = videoRepository.findById(request.videoId())
            .orElseThrow(() -> new NotFoundException("Video", request.videoId()));

        Complaint complaint = new Complaint();
        complaint.setRightsholder(rightsHolder);
        complaint.setVideo(video);
        complaint.setClaimDetails(request.claimDetails());
        complaint.setStatus(ComplaintStatus.RECEIVED);

        return ResponseMapper.toResponse(complaintRepository.save(complaint));
    }

    @Transactional(readOnly = true)
    public Page<ComplaintResponse> getAll(Pageable pageable) {
        User user = getCurrentUser();

        Page<Complaint> page = switch (user.getRole()) {
            case MODERATOR ->
                complaintRepository.findByStatus(
                    ComplaintStatus.PENDING_MODERATOR,
                    pageable
                );

            case AUTHOR ->
                complaintRepository.findByVideo_Author_Id(
                    user.getId(),
                    pageable
                );

            case RIGHTSHOLDER ->
                complaintRepository.findByRightsholder_Id(
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

        if (!hasAccess(getCurrentUser(), complaint))
            throw new ForbiddenException();

        return ResponseMapper.toResponse(complaint);
    }

    @Transactional
    public ComplaintResponse updateComplaint(Long id, ComplaintCreateRequest request) {
        Complaint existing = complaintRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Complaint", id));

        if (!existing.getRightsholder().getId().equals(getCurrentUser().getId()))
            throw new ForbiddenException();

        if (!existing.getStatus().equals(ComplaintStatus.RECEIVED))
            throw new ValidationException("Unable to modify sent complaint");

        if (!existing.getVideo().getId().equals(request.videoId())) {
            Video video = videoRepository.findById(request.videoId())
            .orElseThrow(() -> new NotFoundException("Video", id));

            existing.setVideo(video);
        }

        existing.setClaimDetails(request.claimDetails());

        return ResponseMapper.toResponse(complaintRepository.save(existing));
    }

    @Transactional
    public void deleteComplaint(Long id) {
        Complaint existing = complaintRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Complaint", id));

        if (!existing.getRightsholder().getId().equals(getCurrentUser().getId()))
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
            
            Video toBlock = complaint.getVideo();
            toBlock.setStatus(VideoStatus.BLOCKED_BY_COPYRIGHT);
            videoRepository.save(toBlock);
        } else {
            complaint.setStatus(ComplaintStatus.PENDING_MODERATOR);
        }

        return ResponseMapper.toResponse(complaintRepository.save(complaint));
    }

    private boolean hasAccess(User user, Complaint complaint) {
        return switch (user.getRole()) {
            case RIGHTSHOLDER ->
                complaint.getRightsholder().getId().equals(user.getId());
            
            case AUTHOR ->
                complaint.getVideo().getAuthor().getId().equals(user.getId());

            case MODERATOR ->
                complaint.getStatus().equals(ComplaintStatus.PENDING_MODERATOR);
        };
    }

    @Transactional
    private boolean violatesCopyright(Complaint complaint) {
        List<String> presence = complaint.getVideo().getMusic();
        
        if (presence == null)
            return false;
        
        Set<String> holded = new HashSet<>(
            musicRepository.findNamesByRightsholderId(getCurrentUser().getId())
        );

        return presence.stream().anyMatch(holded::contains);
    }

    @Transactional
    public void acceptViolation(Long complaintId, ModeratorDecisionRequest request) {
        Complaint complaint = complaintRepository.findById(complaintId)
            .orElseThrow(() -> new NotFoundException("Complaint", complaintId));

        Video video = complaint.getVideo();

        complaint.setStatus(ComplaintStatus.ACCEPTED_BY_MODERATOR);
        complaint.setModeratorComment(request.getModeratorComment());
        video.setStatus(VideoStatus.BLOCKED_BY_COPYRIGHT);

        complaintRepository.save(complaint);
        videoRepository.save(video);
    }

    public void rejectViolation(Long complaintId, ModeratorDecisionRequest request) {
        Complaint complaint = complaintRepository.findById(complaintId)
            .orElseThrow(() -> new NotFoundException("Complaint", complaintId));

        complaint.setStatus(ComplaintStatus.REJECTED_BY_MODERATOR);
        complaint.setModeratorComment(request.getModeratorComment());

        complaintRepository.save(complaint);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
            .orElseThrow(() -> new NotFoundException("User with username " + auth.getName() + " not found"));
    }
}
