package com.andrew.service;

import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import com.andrew.dto.document.DocumentCreateDTO;
import com.andrew.dto.document.DocumentResponseDTO;
import com.andrew.exceptions.NotFoundException;
import com.andrew.mapper.dto.DocumentMapper;
import com.andrew.model.DocType;
import com.andrew.model.Document;
import com.andrew.repository.DocumentRepository;
import com.andrew.security.CurrentUser;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.ForbiddenException;

@ApplicationScoped
public class DocumentService {
    @Inject
    DocumentRepository documentRepository;

    @Inject
    DocumentMapper documentMapper;

    @Inject
    CurrentUser currentUser;

    @Inject
    FileStorageService fileStorageService;

    @Transactional
    public DocumentResponseDTO getResponseById(Long id) {
        return documentMapper.toResponse(getById(id));
    }

    @Transactional
    public Document getById(Long id) {
        return documentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Document", id));
    }

    @Transactional
    public DocumentResponseDTO createDocument(DocumentCreateDTO dto) {
        return createDocument(documentMapper.toEntity(dto));
    }

    @Transactional
    public DocumentResponseDTO uploadDocument(DocType docType, InputStream stream, String originalName) {
        String storedFileName = fileStorageService.save(stream, originalName);

        Document document = new Document(currentUser.getUser(), docType, calcValidUntil(docType), storedFileName);
        documentRepository.save(document);

        return documentMapper.toResponse(document);
    }

    @Transactional
    private LocalDate calcValidUntil(DocType docType) {
        return switch (docType) {
            case CAPTAIN_ID -> LocalDate.now().plusYears(5);
            case SHIP_ID -> LocalDate.now().plusYears(5);
            case CARGO_ID -> LocalDate.now().plusWeeks(2);
            case PASS_ID -> LocalDate.now().plusWeeks(1);
        };
    }

    @Transactional
    private DocumentResponseDTO createDocument(Document document) {
        documentRepository.save(document);
        return documentMapper.toResponse(document);
    }

    @Transactional
    public DocumentResponseDTO updateDocument(Long id, DocumentCreateDTO dto) {
        documentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Document", id));

        Document toUpdate = documentMapper.toEntity(dto);
        toUpdate.setId(id);
        
        return documentMapper.toResponse(documentRepository.update(toUpdate));
    }

    @Transactional
    public void deleteDocument(Long id) {
        Document document = documentRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Document", id));

        documentRepository.delete(document);
    }

    @Transactional
    public List<DocumentResponseDTO> getAll() {
        List<Document> documents = switch (currentUser.getUser().getRole()) {
            case ADMIN, KEEPER -> documentRepository.getAll();
            case CAPTAIN -> documentRepository.findByOwnerId(currentUser.getUser().getId());
            default -> throw new ForbiddenException(); 
        };

        return documents.stream().map(documentMapper::toResponse).toList();
    }
}
