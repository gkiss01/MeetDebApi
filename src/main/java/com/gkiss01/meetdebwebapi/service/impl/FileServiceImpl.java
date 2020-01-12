package com.gkiss01.meetdebwebapi.service.impl;

import com.gkiss01.meetdebwebapi.repository.EventRepository;
import com.gkiss01.meetdebwebapi.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@PropertySource("classpath:application.properties")
public class FileServiceImpl implements FileService {

    private final Path fileStorageLocation;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    public FileServiceImpl(@Value("${file.upload.dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e) {
            throw new RuntimeException("Could not create directory!");
        }
    }

    public void storeFile(Long eventId, MultipartFile file) {
        if (!eventRepository.existsEventById(eventId))
            throw new RuntimeException("Event not found!");

        try {
            if (StringUtils.cleanPath(file.getOriginalFilename()).contains(".."))
                throw new RuntimeException("Filename is invalid!");

            Path targetLocation = this.fileStorageLocation.resolve(eventId.toString());
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (IOException ex) {
            throw new RuntimeException("Upload failed!");
        }
    }
    public Resource loadFile(Long eventId) {
        if (!eventRepository.existsEventById(eventId))
            throw new RuntimeException("Event not found!");

        try {
            Path filePath = this.fileStorageLocation.resolve(eventId.toString()).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if(resource.exists()) return resource;
            else throw new RuntimeException("File not found!");

        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found!");
        }
    }
}
