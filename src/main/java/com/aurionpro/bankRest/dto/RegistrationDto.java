package com.aurionpro.bankRest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RegistrationDto {
    @NotNull
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z]{4,16}$", message = "Username should be 4 to 16 character long and should be alphabetic characters")
    private String username;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W])[a-zA-Z\\d\\W]{3,16}$",
            message = """
                    At least one lowercase letter.
                    At least one uppercase letter.
                    At least one digit.
                    At least one special character.
                    The total length of the password is between 3 and 16 characters.""")
    private String password;

    @NotNull
    @NotBlank
    private String role;
}
