package com.manage.contact.service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.manage.contact.dto.ContactDTO;
import com.manage.contact.entity.Contact;
import com.manage.contact.exceptions.InvalidFileException;
import com.manage.contact.repository.ContactRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContactService {
	
	private final ContactRepository contactRepository;
	private final ModelMapper modelMapper;
	private final CsvMapper csvMapper;
	
	@Transactional
	public void uploadContacts(MultipartFile file) throws IOException {
		
		if (file.isEmpty()) {
            throw new InvalidFileException("File is empty");
        }
		
		if (!file.getContentType().equals("text/csv")) {
            throw new InvalidFileException("Please upload a CSV file");
        }
		
		MappingIterator<ContactDTO> contactsIterator = csvMapper
                .readerFor(ContactDTO.class)
                .with(CsvSchema.emptySchema().withHeader())
                .readValues(file.getInputStream());
		
		List<Contact> contacts = contactsIterator.readAll().stream()
                .map(dto -> modelMapper.map(dto, Contact.class))
                .collect(Collectors.toList());
		
		Set<String> uniqueContacts = new HashSet<>();
		 // Validate the contacts list
        for (Contact contact : contacts) {
            if (contact.getName() == null || contact.getName().isEmpty()) {
                throw new InvalidFileException("Invalid CSV structure. A field in name is empty");
            }
            if (contact.getEmail() == null || !contact.getEmail().matches("^[\\w.%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
                throw new InvalidFileException("Invalid CSV structure. A field in Email is empty");
            }
            if (contact.getContactNumber() == null || !contact.getContactNumber().matches("\\d+(-\\d+)*")) {
                throw new InvalidFileException("Invalid CSV structure. Contact is not a valid number");
            }
            if (uniqueContacts.contains(contact.getContactNumber())) {
                throw new InvalidFileException("Invalid CSV structure. Duplicate contact found: " + contact.getContactNumber());
            }
            if(contactRepository.existsByContactNumber(contact.getContactNumber())) {
            	throw new InvalidFileException("Invalid CSV structure. Contact Already Exist: " + contact.getContactNumber());
            }
            uniqueContacts.add(contact.getContactNumber());
        }
		
		contactRepository.saveAll(contacts);
	}
	
	public String fetchContacts(int page, int size) throws Exception {
		PageRequest pageRequest = PageRequest.of(page, size);
		List<ContactDTO> contactList = contactRepository.findAll(pageRequest).stream()
														.map(field -> modelMapper.map(field, ContactDTO.class))
														.collect(Collectors.toList());
		
		// Generate schema from the Employee class
        CsvSchema schema = csvMapper.schemaFor(ContactDTO.class).withHeader();												
        // Write data to a StringWriter
        StringWriter writer = new StringWriter();
        csvMapper.writer(schema).writeValue(writer, contactList);
        
		return writer.toString();
	}
}
