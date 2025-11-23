package net.tao.studentmanagement.service.impl;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.tao.studentmanagement.dto.StudentRequestDto;
import net.tao.studentmanagement.dto.StudentResponseDto;
import net.tao.studentmanagement.exception.ResourceNotFoundException;
import net.tao.studentmanagement.mapper.StudentMapper;
import net.tao.studentmanagement.model.Student;
import net.tao.studentmanagement.repository.StudentRepository;
import net.tao.studentmanagement.service.StudentService;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StudentServiceImpl implements StudentService {

	private final StudentRepository studentRepository;
	private final StudentMapper studentMapper;

	/**
	 * Creates a new student record. Converts the incoming request DTO into an
	 * entity, persists it, and returns a response DTO representing the saved
	 * student.
	 */
	@Override
	public StudentResponseDto createStudent(StudentRequestDto dto) {
		log.info("Creating student: {}", dto.getName());
		Student entity = studentMapper.toEntity(dto);
		studentRepository.save(entity);
		log.info("Student created with id={}", entity.getId());
		return studentMapper.toResponseDto(entity);
	}

	/**
	 * Updates an existing student. Fetches the entity by ID, throws error if not
	 * present, then applies update fields using MapStruct merge logic and saves the
	 * updated record.
	 */
	@Override
	public StudentResponseDto updateStudent(Integer id, StudentRequestDto dto) {
		log.info("Updating student id={}", id);
		Student entity = studentRepository.findById(id).orElseThrow(() -> {
			log.warn("Student not found with id={}", id);
			return new ResourceNotFoundException("Student not found with id: " + id);
		});

		studentMapper.updateEntityFromDto(dto, entity); // MapStruct merge
		studentRepository.save(entity);
		log.info("Updated student id={}", id);
		return studentMapper.toResponseDto(entity);
	}

	/**
	 * Retrieves a single student by ID. Throws ResourceNotFoundException if the
	 * student does not exist.
	 */
	@Override
	@Transactional(readOnly = true)
	public StudentResponseDto getStudentById(Integer id) {
		log.info("Fetching student id={}", id);
		Student entity = studentRepository.findById(id).orElseThrow(() -> {
			log.warn("Student not found id={}", id);
			return new ResourceNotFoundException("Student not found with id: " + id);
		});

		return studentMapper.toResponseDto(entity);
	}

	/**
	 * Returns the complete list of students. Used for simple list rendering without
	 * pagination.
	 */
	@Override
	@Transactional(readOnly = true)
	public List<StudentResponseDto> getAllStudents() {
		log.info("Fetching all students");
		var list = studentRepository.findAll().stream().map(studentMapper::toResponseDto).toList();
		log.info("Fetched {} students", list.size());
		return list;
	}

	/**
	 * Deletes a student by ID. Ensures the student exists before deletion to avoid
	 * silent failures.
	 */
	@Override
	public void deleteStudent(Integer id) {
		log.info("Deleting student id={}", id);

		if (!studentRepository.existsById(id)) {
			log.warn("Cannot delete: student id={} not found", id);
			throw new ResourceNotFoundException("Student not found with id: " + id);
		}

		studentRepository.deleteById(id);
		log.info("Deleted student id={}", id);
	}

	/**
	 * Returns the total number of student records. Used for calculating pagination
	 * page counts.
	 */
	@Override
	public long getTotalCount() {
		long count = studentRepository.count();
	    log.info("Total student count = {}", count);
	    return count;
	}

	/**
	 * Performs a keyword-based search using student name. Supports pagination.
	 */
	@Override
	@Transactional(readOnly = true)
	public List<StudentResponseDto> searchStudents(String keyword, int page, int size) {
		log.info("Searching students. keyword='{}', page={}, size={}", keyword, page, size);

	    var pageable = PageRequest.of(page, size);
	    var list = studentRepository
	            .findByNameContainingIgnoreCase(keyword, pageable)
	            .stream()
	            .map(studentMapper::toResponseDto)
	            .toList();

	    log.info("Search returned {} results for keyword='{}'", list.size(), keyword);
	    return list;
	}

	/**
	 * Returns total number of results for a search query. Required for computing
	 * paginated result counts.
	 */
	@Override
	public long countSearchResults(String keyword) {
		long count = studentRepository.countByNameContainingIgnoreCase(keyword);
	    log.info("Count search results for keyword='{}' = {}", keyword, count);
	    return count;
	}

	/**
	 * Paginated + Sorted student listing. Sort direction (asc/desc) and field are
	 * dynamically configurable.
	 */
	@Transactional(readOnly = true)
	public List<StudentResponseDto> getStudentsPaginatedSorted(int page, int size, String sortField, String sortDir) {
		log.info("Fetching paginated sorted students. page={}, size={}, sortField='{}', sortDir='{}'",
	            page, size, sortField, sortDir);

	    var pageable = PageRequest.of(
	            page,
	            size,
	            sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending()
	    );

	    var list = studentRepository.findAll(pageable)
	            .stream()
	            .map(studentMapper::toResponseDto)
	            .toList();

	    log.info("Paginated + sorted fetch returned {} records", list.size());
	    return list;
	}

	/**
	 * Keyword search + Sorting + Pagination. Combined operation for advanced list
	 * views.
	 */
	@Transactional(readOnly = true)
	public List<StudentResponseDto> searchStudentsSorted(String keyword, int page, int size, String sortField,
			String sortDir) {
		log.info("Searching students (sorted). keyword='{}', page={}, size={}, sortField='{}', sortDir='{}'",
	            keyword, page, size, sortField, sortDir);

	    var pageable = PageRequest.of(
	            page,
	            size,
	            sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending()
	    );

	    var list = studentRepository
	            .findByNameContainingIgnoreCase(keyword, pageable)
	            .stream()
	            .map(studentMapper::toResponseDto)
	            .toList();

	    log.info("Search + sorted returned {} records for keyword='{}'", list.size(), keyword);
	    return list;
	}

}
