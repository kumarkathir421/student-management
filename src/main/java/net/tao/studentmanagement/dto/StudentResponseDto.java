package net.tao.studentmanagement.dto;

import lombok.Data;

@Data
public class StudentResponseDto {

    private Integer id;
    private String name;
    private String address;
    private String gender;
    private String dob;     // formatted as yyyy-MM-dd
    private String email;
    private String mobile;
    private String phone;
}
