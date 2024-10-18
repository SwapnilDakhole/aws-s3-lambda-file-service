package com.example.controller;

import com.example.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/file")
public class StorageController {

    @Autowired
    private StorageService service;

    @PostMapping("/uploadDirectory")
    public ResponseEntity<String> uploadDirectory(
            @RequestParam("directoryPath") String directoryPath,
            @RequestParam("keyPrefix") String keyPrefix) {

        String response = service.uploadDirectory(directoryPath, keyPrefix);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/downloadFolder")
    public ResponseEntity<byte[]> downloadFilesFromFolder(@RequestParam("folderName") String folderName) {
        try {
            byte[] zipFile = service.downloadFolder(folderName);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", folderName + ".zip");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(zipFile);
        } catch (Exception e) {
            return new ResponseEntity("Failed to download files: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        return new ResponseEntity<>(service.deleteFile(fileName), HttpStatus.OK);
    }
}
