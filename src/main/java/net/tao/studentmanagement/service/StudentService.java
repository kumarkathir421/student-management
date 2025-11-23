package net.tao.studentmanagement.service;

import net.tao.studentmanagement.dto.StudentRequestDto;
import net.tao.studentmanagement.dto.StudentResponseDto;
import java.util.List;

public interface StudentService {

	StudentResponseDto createStudent(StudentRequestDto dto);

	StudentResponseDto updateStudent(Integer id, StudentRequestDto dto);

	StudentResponseDto getStudentById(Integer id);

	List<StudentResponseDto> getAllStudents();

	void deleteStudent(Integer id);

	long getTotalCount();

	List<StudentResponseDto> searchStudents(String keyword, int page, int size);

	long countSearchResults(String keyword);

	List<StudentResponseDto> getStudentsPaginatedSorted(int page, int size, String sortField, String sortDir);

	List<StudentResponseDto> searchStudentsSorted(String keyword, int page, int size, String sortField,
			String sortDir);

}
