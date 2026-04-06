package com.example.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucketName;

    public S3Service(S3Client s3Client, @Value("${app.s3.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public void upload(String key, byte[] content, String contentType) {
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(req, RequestBody.fromBytes(content));
    }

    public byte[] download(String key) {
        GetObjectRequest req = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseBytes<?> bytes = s3Client.getObjectAsBytes(req);
        return bytes.asByteArray();
    }

    public List<S3Object> list(String prefix) {
        ListObjectsV2Request.Builder builder = ListObjectsV2Request.builder()
                .bucket(bucketName);

        if (prefix != null && !prefix.isBlank()) {
            builder.prefix(prefix);
        }

        return s3Client.listObjectsV2(builder.build()).contents();
    }
}
