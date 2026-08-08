package com.andrew.service;

import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.StatObjectArgs;
import jakarta.enterprise.context.ApplicationScoped;

import java.io.InputStream;
import java.net.ConnectException;

import com.andrew.exceptions.MinioDownException;
import com.andrew.exceptions.NotFoundException;

@ApplicationScoped
public class MinioService {

    private final MinioClient client;

    public MinioService() {
        client = MinioClient.builder()
                // .endpoint("http://localhost:9000")
                .endpoint("http://localhost:9345")
                .credentials("minioadmin", "minioadmin")
                .build();
    }

    public void uploadFile(String bucket, String objectName, InputStream stream, long size, String contentType) throws Exception {
        try {
            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(stream, size, -1)
                            .contentType(contentType)
                            .build()
            );
        } catch (ConnectException ce) {
            throw new MinioDownException();
        }
    }

    public void rename(String bucket, String sourceObjectName, String targetObjectName) {
        try {
            client.copyObject(
                CopyObjectArgs.builder()
                    .bucket(bucket)
                    .object(targetObjectName)
                    .source(CopySource.builder().bucket("uploads").object(sourceObjectName).build())
                    .build()
            );
            client.removeObject(
                RemoveObjectArgs.builder()
                    .bucket("uploads")
                    .object(sourceObjectName)
                    .build()
            );
        } catch (ConnectException ce) {
            throw new MinioDownException();
        } catch (Exception e) {
            throw new MinioDownException(e.getMessage());
        }
    }

    public InputStream downloadFile(String bucket, String objectName) {
        try {
            return client.getObject(
                GetObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .build()
            );
        } catch (ConnectException ce) {
            throw new MinioDownException();
        } catch (Exception e) {
            throw new NotFoundException("file " + objectName + " was not found");
        }
    }

    public boolean fileExists(String bucket, String objectName) {
        try {
            client.statObject(
                StatObjectArgs.builder()
                        .bucket(bucket)
                        .object(objectName)
                        .build());
            return true;
        } catch (ConnectException ce) {
            throw new MinioDownException();
        } catch (Exception e) {
            return false;
        }
    }

    public void deleteFile(String bucket, String objectName) {
        try {
            client.removeObject(RemoveObjectArgs.builder()
                .bucket(bucket)
                .object(objectName)
                .build());
        } catch (ConnectException ce) {
            throw new MinioDownException();
        } catch (Exception e) {
            throw new MinioDownException(e.getMessage());
        }
    }
}
