package com.andrew.lab1.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.andrew.lab1.dto.complaint.ComplaintCreateRequest;
import com.andrew.lab1.dto.complaint.ComplaintResponse;
import com.andrew.lab1.dto.moderator.ModeratorDecisionRequest;
import com.andrew.lab1.service.ComplaintService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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

@RestController
@RequestMapping("/complaints")
@RequiredArgsConstructor
public class ComplaintController {
    private final ComplaintService complaintService;

    @PostMapping
    @PreAuthorize("hasRole('RIGHTSHOLDER')")
    @ResponseStatus(HttpStatus.CREATED)
    public ComplaintResponse createComplaint(@RequestBody @Valid ComplaintCreateRequest request) {
        return complaintService.createComplaint(request);
    }
    
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Page<ComplaintResponse> getAll(Pageable pageable) {
        return complaintService.getAll(pageable);
    }

    @GetMapping("{id}")
    @PreAuthorize("isAuthenticated()")
    public ComplaintResponse getById(@PathVariable Long id) {
        return complaintService.getById(id);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('RIGHTSHOLDER')")
    public ComplaintResponse updateComplaint(@PathVariable Long id, @RequestBody @Valid ComplaintCreateRequest request) {
        return complaintService.updateComplaint(id, request);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('RIGHTSHOLDER')")
    public void deleteComplaint(@PathVariable Long id) {
        complaintService.deleteComplaint(id);
    }

    @PostMapping("{id}/submit")
    @PreAuthorize("hasRole('RIGHTSHOLDER')")
    public void submitComplaint(@PathVariable Long id) {
        complaintService.submitComplaint(id);
    }

    @PostMapping("{id}/acceptViolation")
    @PreAuthorize("hasRole('MODERATOR')")
    public void acceptViolation(@PathVariable Long id, @RequestBody @Valid ModeratorDecisionRequest request) {
        complaintService.acceptViolation(id, request);
    }

    @PostMapping("{id}/rejectViolation")
    @PreAuthorize("hasRole('MODERATOR')")
    public void rejectViolation(@PathVariable Long id, @RequestBody @Valid ModeratorDecisionRequest request) {
        complaintService.rejectViolation(id, request);
    }
}
