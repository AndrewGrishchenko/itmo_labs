package com.andrew.lab1.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andrew.lab1.dto.video.VideoCreateRequest;
import com.andrew.lab1.dto.video.VideoResponse;
import com.andrew.lab1.entity.Video;
import com.andrew.lab1.entity.XmlUser;
import com.andrew.lab1.entity.enums.VideoStatus;
import com.andrew.lab1.exception.ForbiddenException;
import com.andrew.lab1.exception.NotFoundException;
import com.andrew.lab1.exception.ValidationException;
import com.andrew.lab1.repository.ComplaintRepository;
import com.andrew.lab1.repository.VideoRepository;
import com.andrew.lab1.repository.XmlUserRepository;
import com.andrew.lab1.util.ResponseMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final VideoRepository videoRepository;
    private final XmlUserRepository userRepository;
    private final ComplaintRepository complaintRepository;

    @Transactional
    public VideoResponse createVideo(VideoCreateRequest request) {
        if (videoRepository.findByTitle(request.title()).isPresent())
            throw new ValidationException("Video with title " + request.title() + " already exists");
        
        Video video = new Video();
        video.setTitle(request.title());
        video.setAuthorId(getCurrentUser().getId());
        video.setMusic(request.music());
        video.setStatus(VideoStatus.ACTIVE);

        return ResponseMapper.toResponse(videoRepository.save(video));
    }

    @Transactional(readOnly = true)
    public Page<VideoResponse> getAll(Pageable pageable) {
        return videoRepository.findAll(pageable).map(ResponseMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public VideoResponse getById(Long id) {
        return ResponseMapper.toResponse(videoRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Video", id)));
    }

    @Transactional
    public VideoResponse updateVideo(Long id, VideoCreateRequest request) {
        Video existing = videoRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Video", id));

        if (!existing.getAuthorId().equals(getCurrentUser().getId()))
            throw new ForbiddenException();

        if (!existing.getTitle().equals(request.title())) {
            if (videoRepository.findByTitle(request.title()).isPresent())
                throw new ForbiddenException("Video with that title already exists");
        }

        existing.setTitle(request.title());
        existing.setMusic(request.music());

        return ResponseMapper.toResponse(videoRepository.save(existing));
    }

    @Transactional
    public void deleteVideo(Long id) {
        Video existing = videoRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Video", id));

        if (!existing.getAuthorId().equals(getCurrentUser().getId()))
            throw new ForbiddenException();

        complaintRepository.deleteByVideo_Id(id);
        videoRepository.delete(existing);
    }

    @Transactional
    private XmlUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
            .orElseThrow(() -> new NotFoundException("User with username " + auth.getName() + " not found"));
    }
}
