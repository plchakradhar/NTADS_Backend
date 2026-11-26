package com.networkanomaly.backend.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/model")
@CrossOrigin(origins = "*")
public class ModelController {

    private static final Path MODEL_DIR = Paths.get("models");
    private static final String MODEL_FILENAME = "anomaly_model.joblib";
    private static final String BASELINE_FILENAME = "adaptive_baseline.json";

    private volatile String currentVersion = "0";

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadModels(
            @RequestParam(value = "model", required = false) MultipartFile modelFile,
            @RequestParam(value = "baseline", required = false) MultipartFile baselineFile) throws IOException {
        Files.createDirectories(MODEL_DIR);

        if (modelFile != null && !modelFile.isEmpty()) {
            Files.copy(modelFile.getInputStream(), MODEL_DIR.resolve(MODEL_FILENAME), StandardCopyOption.REPLACE_EXISTING);
        }
        if (baselineFile != null && !baselineFile.isEmpty()) {
            Files.copy(baselineFile.getInputStream(), MODEL_DIR.resolve(BASELINE_FILENAME), StandardCopyOption.REPLACE_EXISTING);
        }

        currentVersion = String.valueOf(Instant.now().toEpochMilli());
        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "version", currentVersion,
                "hasModel", Files.exists(MODEL_DIR.resolve(MODEL_FILENAME)),
                "hasBaseline", Files.exists(MODEL_DIR.resolve(BASELINE_FILENAME))
        ));
    }

    @GetMapping("/version")
    public ResponseEntity<Map<String, Object>> getModelVersion() {
        return ResponseEntity.ok(Map.of(
                "version", currentVersion,
                "hasModel", Files.exists(MODEL_DIR.resolve(MODEL_FILENAME)),
                "hasBaseline", Files.exists(MODEL_DIR.resolve(BASELINE_FILENAME))
        ));
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadModelFile(@PathVariable String filename) throws IOException {
        if (!MODEL_FILENAME.equals(filename) && !BASELINE_FILENAME.equals(filename)) {
            return ResponseEntity.badRequest().build();
        }
        Path filePath = MODEL_DIR.resolve(filename);
        if (!Files.exists(filePath)) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new UrlResource(filePath.toUri());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(resource);
    }
}
