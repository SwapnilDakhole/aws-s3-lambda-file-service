package com.example.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.entity.FileMetadata;
import com.example.repository.FileMetadataRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class StorageService {

    @Value("${application.bucket.name}")
    private String bucketName;

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    private FileMetadataRepository fileMetadataRepository;

    public String uploadDirectory(String directoryPath, String keyPrefix) {
        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            return "The provided path is not a directory.";
        }

        uploadDirectoryRecursively(directory, keyPrefix);
        return "Directory uploaded successfully.";
    }

    // Recursive method to upload directory and its contents to S3
    private void uploadDirectoryRecursively(File directory, String parentPath) {
        for (File file : Objects.requireNonNull(directory.listFiles())) {
            if (file.isFile()) {
                String s3Path = parentPath + file.getName();
                uploadFileToS3(file, s3Path);
            } else if (file.isDirectory()) {
                String newParentPath = parentPath + file.getName() + "/";
                uploadDirectoryRecursively(file, newParentPath);
            }
        }
    }

    // Method to upload an individual file to S3
    private void uploadFileToS3(File file, String s3Path) {
        s3Client.putObject(new PutObjectRequest(bucketName, s3Path, file));
        log.info("Uploaded file: {} to S3 path: {}", file.getAbsolutePath(), s3Path);

        // Retrieve the last modified date from S3 metadata after uploading
        ObjectMetadata objectMetadata = s3Client.getObjectMetadata(bucketName, s3Path);
        LocalDateTime lastModified = objectMetadata.getLastModified()
                .toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        // Save file metadata to the database
        saveFileMetadata(file, s3Path, lastModified);
    }

    // Method to save file metadata to the database
    private void saveFileMetadata(File file, String s3Path, LocalDateTime lastModified) {
        try {
            String fileType = Files.probeContentType(Path.of(file.getPath()));

            FileMetadata fileMetadata = new FileMetadata();
            fileMetadata.setFileName(file.getName());
            fileMetadata.setFileSize(file.length());
            fileMetadata.setFileType(fileType != null ? fileType : "unknown");
            fileMetadata.setS3Path(s3Path);
            fileMetadata.setLastModified(lastModified);

            fileMetadataRepository.save(fileMetadata);
            log.info("Saved metadata for file: {}", file.getName());
        } catch (Exception e) {
            log.error("Error saving file metadata for file: {}", file.getName(), e);
        }
    }



    // Method to download all files from a specified S3 folder as a ZIP
    public byte[] downloadFolder(String folderName) throws Exception {
        // List files in the specified S3 folder
        ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(folderName.endsWith("/") ? folderName : folderName + "/");

        ListObjectsV2Result result;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zipOut = new ZipOutputStream(byteArrayOutputStream)) {

            // Paginate through the list of objects
            do {
                result = s3Client.listObjectsV2(request);

                for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                    String key = objectSummary.getKey();

                    // Skip if the key is the folder itself
                    if (key.endsWith("/")) continue;

                    // Get the file from S3
                    S3Object s3Object = s3Client.getObject(bucketName, key);
                    try (InputStream inputStream = s3Object.getObjectContent()) {
                        // Add file to the ZIP
                        zipOut.putNextEntry(new ZipEntry(key.replaceFirst(folderName, "")));
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = inputStream.read(buffer)) > 0) {
                            zipOut.write(buffer, 0, length);
                        }
                        zipOut.closeEntry();
                    }
                }

                // Update the request for pagination
                request.setContinuationToken(result.getNextContinuationToken());
            } while (result.isTruncated());
        }

        // Return the byte array of the zipped content
        return byteArrayOutputStream.toByteArray();
    }

    // Method to delete a file from S3
    public String deleteFile(String fileName) {
        s3Client.deleteObject(bucketName, fileName);
        return fileName + " removed ...";
    }
}
