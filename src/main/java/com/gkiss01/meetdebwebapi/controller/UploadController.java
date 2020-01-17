package com.gkiss01.meetdebwebapi.controller;

import com.gkiss01.meetdebwebapi.model.GenericResponse;
import com.gkiss01.meetdebwebapi.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "images/{eventId}")
public class UploadController {

    @Autowired
    private FileService fileService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = { "multipart/form-data" })
    public GenericResponse writeFile(@PathVariable Long eventId,
                                     @RequestParam("file") MultipartFile file) {

        fileService.storeFile(eventId, file);
        return GenericResponse.builder().error(false).message("Upload successful!").build();
    }

    @ResponseBody
    @GetMapping
    public ResponseEntity<Resource> readFile(@PathVariable Long eventId) {
        Resource resource = fileService.loadFile(eventId);

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(resource);
    }
}
