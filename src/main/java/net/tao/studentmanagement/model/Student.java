package net.tao.studentmanagement.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class Student {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 45, nullable = false)
	private String name;

	@Column(length = 45)
	private String address;

	@Column(length = 1)
	private String gender = "M";

	@Column
	private LocalDateTime dob;

	@Column(length = 45)
	private String email;

	@Column(length = 15)
	private String mobile;

	@Column(length = 15)
	private String phone;
}
