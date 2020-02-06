package com.flightbuddy.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Getter
class RegistrationRequest {

	@NotBlank
	@Email
	private final String username;

	@NotBlank
	@Size(min = 8)
	private final String  password;
}
