package com.aurionpro.bankRest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerDto {
	private int customerId;
	private String firstName;
	private String lastName;
	private String email;
}
