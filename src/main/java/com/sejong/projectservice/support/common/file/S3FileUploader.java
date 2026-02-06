package com.sejong.projectservice.support.common.file;

import com.sejong.projectservice.support.common.exception.BaseException;
import com.sejong.projectservice.support.common.exception.ExceptionType;
import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

/**
 * S3 호환 스토리지 (Garage) 파일 업로더
 * <p>
 * 지원 방식: 1. Presigned URL - generatePreSignedUrl() + moveFile() 2. 직접 업로드 - uploadFile()
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class S3FileUploader implements FileUploader {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Value("${app.file.base-url}")
    private String baseUrl;

    @Value("${spring.application.name}")
    private String serviceName;

    // ==================== 공통 ====================

    /**
     * 파일 삭제 (내부 스토리지 파일만)
     */
    @Override
    public void delete(String keyOrUrl) {
        if (keyOrUrl == null || isExternalUrl(keyOrUrl)) {
            log.warn("삭제 스킵 - null이거나 외부 URL: {}", keyOrUrl);
            return;
        }
        try {
            String key = keyOrUrl.startsWith("http") ? extractKeyFromUrl(keyOrUrl) : keyOrUrl;
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3Client.deleteObject(deleteRequest);
            log.info("S3 파일 삭제 완료: {}", key);
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패: {}", keyOrUrl, e);
            throw new BaseException(ExceptionType.FILE_REMOVE_FAIL);
        }
    }

    /**
     * key 또는 외부 URL을 전체 URL로 변환 - 외부 URL (http://, https://): 그대로 반환 - 내부 key: baseUrl/key 형태로 조립 (버킷명은 nginx에서 Host
     * 헤더로 처리)
     */
    @Override
    public String getFileUrl(String keyOrUrl) {
        if (keyOrUrl == null) {
            return null;
        }
        if (isExternalUrl(keyOrUrl)) {
            return keyOrUrl;
        }
        return String.format("%s/%s", baseUrl, keyOrUrl);
    }

    /**
     * 외부 URL인지 판단
     */
    private boolean isExternalUrl(String value) {
        return value.startsWith("http://") || value.startsWith("https://");
    }

    // ==================== Presigned URL 방식 ====================

    /**
     * Presigned URL 생성 - temp 폴더에 업로드 후, 저장 시 moveFile()로 최종 위치 이동
     */
    @Override
    public PreSignedUrl generatePreSignedUrl(String fileName, String contentType, String fileType) {
        String key = generateTempKey(serviceName, fileType, fileName);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        String downloadUrl = String.format("%s/%s", baseUrl, key);

        return new PreSignedUrl(
                presignedRequest.url().toString(),
                key,
                downloadUrl,
                System.currentTimeMillis() + Duration.ofMinutes(10).toMillis()
        );
    }

    /**
     * Presigned URL용 key 생성 (temp 폴더 포함)
     */
    private String generateTempKey(String serviceName, String dirName, String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        String fileExtension = lastDotIndex != -1 ? fileName.substring(lastDotIndex) : "";
        String uuid = UUID.randomUUID().toString();
        String cleanFileName = lastDotIndex != -1 ? fileName.substring(0, lastDotIndex) : fileName;
        return String.format("temp/%s/%s/%s_%s%s", serviceName, dirName, cleanFileName, uuid, fileExtension);
    }

    /**
     * URL에서 key 추출
     */
    @Override
    public String extractKeyFromUrl(String url) {
        if (url == null) {
            return null;
        }
        if (url.startsWith(baseUrl)) {
            String key = url.substring(baseUrl.length());
            return key.startsWith("/") ? key.substring(1) : key;
        }
        return url;
    }

    /**
     * temp 파일을 최종 위치로 이동 (복사 후 원본 삭제)
     */
    @Override
    public String moveFile(String sourceKey, String targetDirectory) {
        if (sourceKey == null || sourceKey.isBlank()) {
            log.warn("moveFile 스킵 - sourceKey가 null 또는 빈 값");
            return null;
        }

        String fileName = sourceKey.substring(sourceKey.lastIndexOf("/") + 1);
        String targetKey = String.format("%s/%s", targetDirectory, fileName);

        try {
            s3Client.copyObject(CopyObjectRequest.builder()
                    .sourceBucket(bucketName)
                    .sourceKey(sourceKey)
                    .destinationBucket(bucketName)
                    .destinationKey(targetKey)
                    .build());

            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(sourceKey)
                    .build());

            log.info("S3 파일 이동 완료: {} -> {}", sourceKey, targetKey);
            return targetKey;

        } catch (Exception e) {
            log.error("S3 파일 이동 실패: {} -> {}", sourceKey, targetDirectory, e);
            throw new BaseException(ExceptionType.FILE_MOVE_FAIL);
        }
    }

    // ==================== 직접 업로드 방식 ====================

    /**
     * 파일 직접 업로드 (temp 거치지 않고 바로 최종 위치)
     */
    @Override
    public String uploadFile(MultipartFile file, String directory, String fileName) {
        try {
            String key = generateDirectUploadKey(directory, fileName);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            log.info("S3 파일 업로드 완료: {}", key);
            return key;

        } catch (IOException e) {
            log.error("파일 업로드 실패: {}", fileName, e);
            throw new BaseException(ExceptionType.FILE_UPLOAD_FAIL);
        }
    }

    /**
     * 직접 업로드용 key 생성
     */
    private String generateDirectUploadKey(String directory, String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        String fileExtension = lastDotIndex != -1 ? fileName.substring(lastDotIndex) : "";
        String uuid = UUID.randomUUID().toString();
        String cleanFileName = lastDotIndex != -1 ? fileName.substring(0, lastDotIndex) : fileName;
        return String.format("%s/%s_%s%s", directory, cleanFileName, uuid, fileExtension);
    }
}
