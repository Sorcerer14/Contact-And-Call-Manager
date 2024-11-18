package com.manage.contact.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ContactDTO {
	@JsonProperty("name")
	private String name;
	@JsonProperty("country_code")
    private String countryCode;
	@JsonProperty("contact_number")
    private String contactNumber;
	@JsonProperty("email")
    private String email;
}
