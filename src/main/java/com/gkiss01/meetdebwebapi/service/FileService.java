package com.gkiss01.meetdebwebapi.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    void storeFile(Long eventId, MultipartFile file);
    Resource loadFile(Long eventId);
    void deleteFile(Long eventId);
}
