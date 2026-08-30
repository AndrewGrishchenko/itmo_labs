package com.andrew.lab2.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andrew.lab2.dto.music.MusicCreateRequest;
import com.andrew.lab2.dto.music.MusicResponse;
import com.andrew.lab2.entity.studs.Music;
import com.andrew.lab2.exception.ForbiddenException;
import com.andrew.lab2.exception.NotFoundException;
import com.andrew.lab2.exception.ValidationException;
import com.andrew.lab2.repository.studs.MusicRepository;
import com.andrew.lab2.util.ResponseMapper;
import com.andrew.lab2.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MusicService {
    private final MusicRepository musicRepository;

    @Transactional
    public MusicResponse createMusic(MusicCreateRequest request) {
        if (musicRepository.findByName(request.name()).isPresent())
            throw new ValidationException("Music with name " + request.name() + " already exists");

        Music music = new Music();
        music.setName(request.name());
        music.setRightsholderId(SecurityUtils.getCurrentUserId());

        return ResponseMapper.toResponse(musicRepository.save(music));
    }

    @Transactional(readOnly = true)
    public Page<MusicResponse> getAll(Pageable pageable) {
        return musicRepository.findAll(pageable).map(ResponseMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public MusicResponse getById(Long id) {
        return ResponseMapper.toResponse(musicRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Music", id)));
    }

    @Transactional
    public MusicResponse updateMusic(Long id, MusicCreateRequest request) {
        Music existing = musicRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Music", id));

        if (!existing.getRightsholderId().equals(SecurityUtils.getCurrentUserId()))
            throw new ForbiddenException();

        existing.setName(request.name());

        return ResponseMapper.toResponse(musicRepository.save(existing));
    }

    @Transactional
    public void deleteMusic(Long id) {
        Music existing = musicRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Music", id));

        if (!existing.getRightsholderId().equals(SecurityUtils.getCurrentUserId()))
            throw new ForbiddenException();

        musicRepository.delete(existing);
    }
}
