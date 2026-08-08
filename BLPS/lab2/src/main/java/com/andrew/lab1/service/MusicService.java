package com.andrew.lab1.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.andrew.lab1.dto.music.MusicCreateRequest;
import com.andrew.lab1.dto.music.MusicResponse;
import com.andrew.lab1.entity.Music;
import com.andrew.lab1.entity.XmlUser;
import com.andrew.lab1.exception.ForbiddenException;
import com.andrew.lab1.exception.NotFoundException;
import com.andrew.lab1.exception.ValidationException;
import com.andrew.lab1.repository.MusicRepository;
import com.andrew.lab1.repository.XmlUserRepository;
import com.andrew.lab1.util.ResponseMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MusicService {
    private final MusicRepository musicRepository;
    private final XmlUserRepository userRepository;

    @Transactional
    public MusicResponse createMusic(MusicCreateRequest request) {
        if (musicRepository.findByName(request.name()).isPresent())
            throw new ValidationException("Music with name " + request.name() + " already exists");

        Music music = new Music();
        music.setName(request.name());
        music.setRightsholderId(getCurrentUser().getId());

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

        if (!existing.getRightsholderId().equals(getCurrentUser().getId()))
            throw new ForbiddenException();

        existing.setName(request.name());

        return ResponseMapper.toResponse(musicRepository.save(existing));
    }

    @Transactional
    public void deleteMusic(Long id) {
        Music existing = musicRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Music", id));

        if (!existing.getRightsholderId().equals(getCurrentUser().getId()))
            throw new ForbiddenException();

        musicRepository.delete(existing);
    }

    @Transactional
    private XmlUser getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
            .orElseThrow(() -> new NotFoundException("User with username " + auth.getName() + " not found"));
    }
}
