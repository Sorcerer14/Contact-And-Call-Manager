package com.manage.contact.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.manage.contact.exceptions.InvalidFileException;
import com.manage.contact.service.ContactService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
@Tag(name = "Contact Controller API")
public class ContactController {
	
	private final ContactService contactService;
	
	@PostMapping(
	        value = "/upload",
	        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
	    )
	public ResponseEntity<?> uploadCSV(@RequestParam("file") MultipartFile file){
		try {
            contactService.uploadContacts(file);
            return ResponseEntity.ok(new ApiResponse(true, "Contacts uploaded successfully"));
        } catch (InvalidFileException e) {  // Now this will catch our custom exceptions
            return ResponseEntity.badRequest()
                .body(new ApiResponse(false, "File validation failed: " + e.getMessage()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Error processing file: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(false, "Unexpected error: " + e.getMessage()));
        }
	}
	
	@GetMapping(
				value = "/export",
				produces = "text/csv"
			)
	public  ResponseEntity<?> exportCSV(
				@RequestParam(defaultValue = "0") int page,
	            @RequestParam(defaultValue = "100") int size
			){
		try {
			String csvContent = contactService.fetchContacts(page, size);
			return ResponseEntity.ok()
		            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"contacts.csv\"")
		            .contentType(new MediaType("text", "csv", StandardCharsets.UTF_8))
		            .body(csvContent.getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(new ApiResponse(false, "Unexpected error: " + e.getMessage()));
		}
	}
}
