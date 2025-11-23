package net.tao.studentmanagement.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StudentRequestDto {

    @NotBlank(message = "Name is required")
    @Size(max = 45)
    private String name;

    @Size(max = 45)
    private String address;

    @Pattern(regexp = "^[MF]$", message = "Gender must be M or F")
    private String gender;

    @Past(message = "DOB must be a past date")
    private LocalDate dob;

    @Email
    @Size(max = 45)
    private String email;

    @Size(max = 15)
    private String mobile;

    @Size(max = 15)
    private String phone;
}
