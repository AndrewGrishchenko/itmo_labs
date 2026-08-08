package com.andrew.controller;

import java.io.InputStream;
import java.nio.file.Files;

import com.andrew.annotation.RequireRole;
import com.andrew.dto.document.DocumentResponseDTO;
import com.andrew.model.DocType;
import com.andrew.model.Document;
import com.andrew.model.Role;
import com.andrew.service.DocumentService;
import com.andrew.service.FileStorageService;

import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.EntityPart;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;

@Path("/document")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DocumentController {
    @Inject
    DocumentService documentService;

    @Inject
    FileStorageService fileStorageService;

    @GET
    @RequireRole({Role.CAPTAIN, Role.KEEPER, Role.BOSS})
    public Response getDocuments(@PathParam("id") Long id) {
        return Response.ok(documentService.getAll()).build();
    }

    @POST
    @RequireRole(Role.CAPTAIN)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadDocument(
        @FormParam("file") EntityPart filePart,
        @FormParam("docType") String doc
    ) {
        DocType docType = DocType.from(doc).orElseThrow(() -> new BadRequestException());
        
        if (filePart == null)
            return Response.status(Response.Status.BAD_REQUEST).build();
        
        InputStream stream = filePart.getContent();
        String originalName = filePart.getFileName().orElse("unknown");
        DocumentResponseDTO respone = documentService.uploadDocument(docType, stream, originalName);

        return Response.ok(respone).build();
    }

    @GET
    @Path("{id}")
    @RequireRole({Role.CAPTAIN, Role.KEEPER, Role.BOSS})
    public Response getById(@PathParam("id") Long id) {
        return Response.ok(documentService.getResponseById(id)).build();
    }

    @GET
    @Path("{id}/download")
    @RequireRole({Role.CAPTAIN, Role.KEEPER, Role.BOSS})
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getImage(@PathParam("id") Long id) {
        Document document = documentService.getById(id);

        java.nio.file.Path path = fileStorageService.resolve(document.getFilePath());

        StreamingOutput stream = output -> {
            Files.copy(path, output);
        };

        return Response.ok(stream)
            .header("Content-Disposition",
                "attachment; filename=\"" + path.getFileName() + "\"")
            .build();
    }
}
