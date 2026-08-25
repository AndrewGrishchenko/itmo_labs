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

import com.andrew.lab2.dto.music.MusicCreateRequest;
import com.andrew.lab2.dto.music.MusicResponse;
import com.andrew.lab2.service.MusicService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/music")
@RequiredArgsConstructor
public class MusicController {
    private final MusicService musicService;

    @PostMapping
    @PreAuthorize("hasRole('RIGHTSHOLDER')")
    @ResponseStatus(HttpStatus.CREATED)
    public MusicResponse createMusic(@RequestBody @Valid MusicCreateRequest request) {
        return musicService.createMusic(request);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Page<MusicResponse> getAll(Pageable pageable) {
        return musicService.getAll(pageable);
    }

    @GetMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public MusicResponse getById(@PathVariable Long id) {
        return musicService.getById(id);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('RIGHTSHOLDER')")
    public MusicResponse updateMusic(@PathVariable Long id, @RequestBody @Valid MusicCreateRequest request) {
        return musicService.updateMusic(id, request);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('RIGHTSHOLDER')")
    public void deleteMusic(@PathVariable Long id) {
        musicService.deleteMusic(id);
    }
}
