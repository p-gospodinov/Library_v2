package com.example.project.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileService {
//    private final UserStatusService userStatusService;
//    private final String UPLOAD_DIR = "uploads/";
//
//    public FileService(UserStatusService userStatusService) {
//        this.userStatusService = userStatusService;
//    }
//
//    public String uploadFile(MultipartFile file) throws IOException {
//        if (!userStatusService.isCurrentUserActive()) {
//            throw new IllegalStateException("User is not active and cannot perform this action.");
//        }
//
//        Path uploadPath = Paths.get(UPLOAD_DIR);
//        if (!Files.exists(uploadPath)) {
//            Files.createDirectories(uploadPath);
//        }
//
//        // Construct the file path
//        String fileName = file.getOriginalFilename();
//        Path filePath = uploadPath.resolve(fileName);
//
//        // Save the file to the server
//        Files.copy(file.getInputStream(), filePath);
//
//        // Return the relative file path
//        return filePath.toString();
//    }

//    public Resource downloadFile(String filename) throws MalformedURLException {
//        // Get the file path
//        Path filePath = this.uploadDir.resolve(filename).normalize();
//
//        // Load the file as a resource
//        Resource resource = new UrlResource(filePath.toUri());
//
//        if (resource.exists() && resource.isReadable()) {
//            return resource;
//        } else {
//            throw new RuntimeException("File not found or not readable: " + filename);
//        }
//    }
}
