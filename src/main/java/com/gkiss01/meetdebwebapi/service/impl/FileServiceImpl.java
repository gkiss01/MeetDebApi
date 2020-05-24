package com.gkiss01.meetdebwebapi.service.impl;

import com.gkiss01.meetdebwebapi.repository.EventRepository;
import com.gkiss01.meetdebwebapi.service.FileService;
import com.gkiss01.meetdebwebapi.utils.CustomFileNotFoundException;
import com.gkiss01.meetdebwebapi.utils.CustomRuntimeException;
import com.gkiss01.meetdebwebapi.utils.ErrorCodes;
import com.gkiss01.meetdebwebapi.utils.ImageConverter;
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

@Service
@PropertySource("classpath:application.properties")
public class FileServiceImpl implements FileService {

    private final Path fileStorageLocation;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    public FileServiceImpl(@Value("${file.upload.dir}") String uploadDir) {
        fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(fileStorageLocation);
        } catch (Exception e) {
            throw new CustomRuntimeException(ErrorCodes.COULD_NOT_CREATE_DIRECTORY);
        }
    }

    public void storeFile(Long eventId, MultipartFile file) {
        if (!eventRepository.existsEventById(eventId))
            throw new CustomRuntimeException(ErrorCodes.EVENT_NOT_FOUND);

        try {
            if (file.getOriginalFilename() != null && StringUtils.cleanPath(file.getOriginalFilename()).contains(".."))
                throw new CustomRuntimeException(ErrorCodes.FILENAME_INVALID);

            Path targetLocation = fileStorageLocation.resolve(eventId.toString().concat(".jpeg"));
            boolean result = ImageConverter.convertFormat(file.getInputStream(), targetLocation.toString(), "jpeg");
            if (!result)
                throw new CustomRuntimeException(ErrorCodes.COULD_NOT_CONVERT_IMAGE);
        }
        catch (IOException ex) {
            throw new CustomRuntimeException(ErrorCodes.UPLOAD_FAILED);
        }
    }

    public Resource loadFile(Long eventId) {
        try {
            Resource resource = new UrlResource(fileStorageLocation.resolve(eventId.toString().concat(".jpeg")).normalize().toUri());

            if (resource.exists()) return resource;
            else throw new CustomFileNotFoundException(ErrorCodes.FILE_NOT_FOUND);
        }
        catch (MalformedURLException e) {
            throw new CustomFileNotFoundException(ErrorCodes.FILE_NOT_FOUND);
        }
    }

    @Override
    public void deleteFile(Long eventId) {
        if (!eventRepository.existsEventById(eventId)) return;
            //throw new CustomRuntimeException(ErrorCodes.EVENT_NOT_FOUND);

        Path location = fileStorageLocation.resolve(eventId.toString().concat(".jpeg"));
        try {
            Files.delete(location);
        }
        catch (IOException ignored) { }
    }
}
