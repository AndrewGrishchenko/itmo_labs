package com.andrew.lab2.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.andrew.lab2.dto.video.VideoCreateRequest;
import com.andrew.lab2.dto.video.VideoResponse;
import com.andrew.lab2.service.VideoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/videos")
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;

    @PostMapping
    @PreAuthorize("hasRole('AUTHOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public VideoResponse createVideo(@RequestBody @Valid VideoCreateRequest request) {
        return videoService.createVideo(request);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Page<VideoResponse> getAll(Pageable pageable) {
        return videoService.getAll(pageable);
    }

    @GetMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public VideoResponse getById(@PathVariable Long id) {
        return videoService.getById(id);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('AUTHOR')")
    public VideoResponse updateVideo(@PathVariable Long id, VideoCreateRequest request) {
        return videoService.updateVideo(id, request);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('AUTHOR')")
    public void deleteVideo(@PathVariable Long id) {
        videoService.deleteVideo(id);
    }
}
