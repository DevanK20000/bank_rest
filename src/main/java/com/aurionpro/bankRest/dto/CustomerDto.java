package com.aurionpro.bankRest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerDto {
	@NotNull
	@NotBlank
	private int customerId;
	@NotNull
	@NotBlank
	@Pattern(regexp = "^[a-zA-Z]{2,16}$")
	private String firstName;
	@NotNull
	@NotBlank
	@Pattern(regexp = "^[a-zA-Z]{2,16}$")
	private String lastName;
	@NotNull
	@NotBlank
	@Email
	private String email;

	@NotNull
	@NotBlank
	private boolean active;
}
