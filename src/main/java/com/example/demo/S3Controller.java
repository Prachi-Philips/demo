package com.example.demo;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/s3")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file,
                                         @RequestParam(value = "key", required = false) String key) throws IOException {
        String objectKey = (key == null || key.isBlank()) ? file.getOriginalFilename() : key;
        if (objectKey == null || objectKey.isBlank()) {
            return ResponseEntity.badRequest().body("Missing key");
        }

        s3Service.upload(objectKey, file.getBytes(), file.getContentType());
        return ResponseEntity.ok(objectKey);
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> download(@RequestParam("key") String key) {
        byte[] bytes = s3Service.download(key);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + key + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }

    @GetMapping("/list")
    public List<String> list(@RequestParam(value = "prefix", required = false) String prefix) {
        List<S3Object> objs = s3Service.list(prefix);
        return objs.stream().map(S3Object::key).toList();
    }
}
