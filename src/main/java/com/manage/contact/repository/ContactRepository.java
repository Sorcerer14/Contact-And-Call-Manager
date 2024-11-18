package com.manage.contact.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.manage.contact.entity.Contact;

public interface ContactRepository extends JpaRepository<Contact, Long> {
	
	boolean existsByContactNumber(String contactNumber);
	
	Page<Contact> findAll(Pageable pageable);
}
