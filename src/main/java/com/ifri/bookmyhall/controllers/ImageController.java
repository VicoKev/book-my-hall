package com.ifri.bookmyhall.controllers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/images")
@Slf4j
public class ImageController {

    @Value("${app.upload-dir}")
    private String uploadDir;

    @GetMapping("/salles/{filename:.+}")
    public ResponseEntity<Resource> getSalleImage(@PathVariable String filename) {
        try {
            Path file = Paths.get(uploadDir).resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                String contentType = Files.probeContentType(file);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                log.warn("Impossible de lire le fichier: {}", filename);
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            log.error("Erreur lors de la lecture du fichier: {}", filename, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}