package com.sharib.nttuc1;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/docs")
public class Controller {
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private Service service;

	public Controller(Service service) {
		this.service = service;
	}

	private static final List<String> ALLOWED_TYPES = Arrays.asList(
            MediaType.APPLICATION_PDF_VALUE,
            MediaType.TEXT_PLAIN_VALUE
    );

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) {
		log.info("Uploading file: " + file.getOriginalFilename());
        // 1. Check if the file is empty
        if (file.isEmpty()) {
	        log.error("Upload failed: file.isEmpty() = " + file.isEmpty());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Upload failed: Please select a file to upload.");
        }
	    long largeFileContent = 1024 * 10;
	    if (file.getSize() > largeFileContent) {
		    log.error("Upload failed: file.getSize() = " + file.getSize());
		    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				    .body("Upload failed: Please select a smaller file to upload.");
	    }

        // 2. Validate file type (PDF or TXT)
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
	        log.error("Upload failed: Only PDF and Plain Text files are allowed.");
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body("Upload failed: Only PDF and Plain Text files are allowed.");
        }

        try {
            // 3. Process the file (Example: Log metadata or extract bytes)
            String fileName = file.getOriginalFilename();
            long fileSize = file.getSize();
            // save file to SimpleVectorStore
	        service.uploadDocument(file);
	        log.info("Uploaded file: " + file.getOriginalFilename());
            return ResponseEntity.ok(String.format("File '%s' (%d bytes) uploaded successfully!", fileName, fileSize));
        } catch (Exception e) {
	        log.error("Error while uploading file: {}", file.getOriginalFilename(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed due to a server error.");
        }
    }

	@GetMapping("/query/{text}")
	public String queryDocuments(@PathVariable String text) {
		log.info("queryDocuments: " + text);
		return service.queryDocuments(text);
	}


}
