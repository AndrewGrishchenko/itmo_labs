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
import com.andrew.lab2.entity.xml.XmlUser;
import com.andrew.lab2.entity.xml.XmlUserDetails;
import com.andrew.lab2.exception.ForbiddenException;
import com.andrew.lab2.exception.NotFoundException;
import com.andrew.lab2.exception.ValidationException;
import com.andrew.lab2.notification.NotificationEventType;
import com.andrew.lab2.notification.NotificationProducer;
import com.andrew.lab2.notification.dto.NotificationPayload;
import com.andrew.lab2.repository.studs.ComplaintRepository;
import com.andrew.lab2.repository.studs.MusicRepository;
import com.andrew.lab2.repository.studs2.VideoRepository;
import com.andrew.lab2.repository.xml.XmlUserRepository;
import com.andrew.lab2.util.ResponseMapper;
import com.andrew.lab2.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComplaintService {
    private final ComplaintRepository complaintRepository;
    private final VideoRepository videoRepository;
    private final MusicRepository musicRepository;
    private final XmlUserRepository xmlUserRepository;
    private final NotificationProducer notificationProducer;
    private final ComplaintScheduler complaintScheduler;

    @Transactional
    public ComplaintResponse createComplaint(ComplaintCreateRequest request) {
        XmlUserDetails rightsHolder = SecurityUtils.getCurrentPrincipal();
        
        Video video = videoRepository.findById(request.videoId())
            .orElseThrow(() -> new NotFoundException("Video", request.videoId()));

        if (video.getStatus() == VideoStatus.BLOCKED_BY_COPYRIGHT)
            throw new ValidationException("Video is blocked");

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

        XmlUserDetails rightsHolder = SecurityUtils.getCurrentPrincipal();

        if (violatesCopyright(complaint)) {
            complaint.setStatus(ComplaintStatus.ACCEPTED_BY_AUTO);
            
            Video toBlock = videoRepository.findById(complaint.getVideoId())
                .orElseThrow(() -> new NotFoundException("Video", id));

            toBlock.setStatus(VideoStatus.BLOCKED_BY_COPYRIGHT);
            videoRepository.save(toBlock);

            notificationProducer.sendNotification(
                NotificationEventType.CONTENT_AUTO_BLOCKED,
                rightsHolder.getId(),
                rightsHolder.getEmail(),
                NotificationPayload.forBlockedContent(complaint, toBlock)
            );

            XmlUser author = xmlUserRepository.findById(toBlock.getAuthorId())
                .orElseThrow(() -> new NotFoundException("User", toBlock.getAuthorId()));
            notificationProducer.sendNotification(
                NotificationEventType.CONTENT_AUTO_BLOCKED,
                author.getId(),
                author.getEmail(),
                NotificationPayload.forBlockedContent(complaint, toBlock)
            );
        } else {
            complaint.setStatus(ComplaintStatus.PENDING_MODERATOR);

            notificationProducer.sendNotification(
                NotificationEventType.CONTENT_SENT_TO_MODERATION,
                rightsHolder.getId(),
                rightsHolder.getEmail(),
                NotificationPayload.forComplaint(complaint)
            );

            complaintScheduler.scheduleExpiration(complaint.getId());
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
        Complaint complaint = complaintRepository.findById(complaintId)
            .orElseThrow(() -> new NotFoundException("Complaint", complaintId));

        if (complaint.getStatus() != ComplaintStatus.PENDING_MODERATOR)
            throw new ValidationException("Complaint is not pending moderator");

        Video video = videoRepository.findById(complaint.getVideoId())
            .orElseThrow(() -> new NotFoundException("Video", complaint.getVideoId()));

        complaint.setStatus(ComplaintStatus.ACCEPTED_BY_MODERATOR);
        complaint.setModeratorComment(request.getModeratorComment());
        video.setStatus(VideoStatus.BLOCKED_BY_COPYRIGHT);

        complaintRepository.save(complaint);
        videoRepository.save(video);

        XmlUser rightsholder = xmlUserRepository.findById(complaint.getRightsholderId())
            .orElseThrow(() -> new NotFoundException("User", complaint.getRightsholderId()));
        XmlUser author = xmlUserRepository.findById(video.getAuthorId())
            .orElseThrow(() -> new NotFoundException("User", video.getAuthorId()));

        notificationProducer.sendNotification(
            NotificationEventType.COMPLAINT_SATISFIED,
            rightsholder.getId(),
            rightsholder.getEmail(),
            NotificationPayload.forBlockedContent(complaint, video)
        );

        notificationProducer.sendNotification(
            NotificationEventType.COMPLAINT_SATISFIED,
            author.getId(),
            author.getEmail(),
            NotificationPayload.forBlockedContent(complaint, video)
        );

        // throw new RuntimeException("test rollback");
    }

    @Transactional
    public void rejectViolation(Long complaintId, ModeratorDecisionRequest request) {
        Complaint complaint = complaintRepository.findById(complaintId)
            .orElseThrow(() -> new NotFoundException("Complaint", complaintId));

        complaint.setStatus(ComplaintStatus.REJECTED_BY_MODERATOR);
        complaint.setModeratorComment(request.getModeratorComment());

        complaintRepository.save(complaint);

        XmlUser rightsholder = xmlUserRepository.findById(complaint.getRightsholderId())
            .orElseThrow(() -> new NotFoundException("User", complaint.getRightsholderId()));
        notificationProducer.sendNotification(
            NotificationEventType.COMPLAINT_REJECTED,
            rightsholder.getId(),
            rightsholder.getEmail(),
            NotificationPayload.forComplaint(complaint)
        );
    }

    @Transactional 
    public void expireIfPending(Long complaintId) {
        Complaint complaint = complaintRepository.findById(complaintId)
            .orElseThrow(() -> new NotFoundException("Complaint", complaintId));

        if (complaint.getStatus() != ComplaintStatus.PENDING_MODERATOR)
            return;

        complaint.setStatus(ComplaintStatus.EXPIRED);
        complaintRepository.save(complaint);

        XmlUser rightsholder = xmlUserRepository.findById(complaint.getRightsholderId())
            .orElseThrow(() -> new NotFoundException("User", complaint.getRightsholderId()));
        notificationProducer.sendNotification(
            NotificationEventType.COMPLAINT_EXPIRED,
            rightsholder.getId(),
            rightsholder.getEmail(),
            NotificationPayload.forComplaint(complaint)
        );
    }
}
