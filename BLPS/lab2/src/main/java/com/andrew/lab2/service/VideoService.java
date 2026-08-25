package com.andrew.lab2.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andrew.lab2.dto.video.VideoCreateRequest;
import com.andrew.lab2.dto.video.VideoResponse;
import com.andrew.lab2.entity.enums.VideoStatus;
import com.andrew.lab2.entity.studs2.Video;
import com.andrew.lab2.exception.ForbiddenException;
import com.andrew.lab2.exception.NotFoundException;
import com.andrew.lab2.exception.ValidationException;
import com.andrew.lab2.repository.studs.ComplaintRepository;
import com.andrew.lab2.repository.studs2.VideoRepository;
import com.andrew.lab2.util.ResponseMapper;
import com.andrew.lab2.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VideoService {
    private final VideoRepository videoRepository;
    private final ComplaintRepository complaintRepository;

    @Transactional
    public VideoResponse createVideo(VideoCreateRequest request) {
        if (videoRepository.findByTitle(request.title()).isPresent())
            throw new ValidationException("Video with title " + request.title() + " already exists");
        
        Video video = new Video();
        video.setTitle(request.title());
        video.setAuthorId(SecurityUtils.getCurrentUserId());
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

        if (!existing.getAuthorId().equals(SecurityUtils.getCurrentUserId()))
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

        if (!existing.getAuthorId().equals(SecurityUtils.getCurrentUserId()))
            throw new ForbiddenException();

        complaintRepository.deleteByVideoId(id);
        videoRepository.delete(existing);
    }
}
