package net.tao.studentmanagement.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.tao.studentmanagement.dto.StudentRequestDto;
import net.tao.studentmanagement.dto.StudentResponseDto;
import net.tao.studentmanagement.service.StudentService;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/students")
public class StudentController {

	private static final String SUCCESS_MESSAGE = "successMessage";
	private static final String TITLE = "title";
	private static final String PAGE_URL = "redirect:/students/list?page=";
	
	@Value("${app.pagination.default-size}")
	private int defaultSize;
	
	private final StudentService studentService;

	/**
	 * Displays the student list with pagination and optional keyword search.
	 *
	 * @param page    Current page number (0-based)
	 * @param size    Number of records per page
	 * @param keyword Optional search text for filtering by name/id
	 * @param model   Spring Model to pass UI attributes
	 * @return Thymeleaf view for listing students
	 */
	@GetMapping("/list")
	public String listStudents(@RequestParam(defaultValue = "0") int page, @RequestParam(required = false) Integer size,
			@RequestParam(defaultValue = "") String keyword, @RequestParam(defaultValue = "id") String sortField,
			@RequestParam(defaultValue = "asc") String sortDir, Model model) {
		log.info("Listing students: page={}, size={}, keyword='{}', sortField='{}', sortDir='{}'", page, size, keyword,
				sortField, sortDir);
		if (page < 0)
			page = 0;
		if(size == null)
			size = defaultSize;

		boolean hasSearch = keyword != null && !keyword.isBlank();

		List<StudentResponseDto> students;
		long total;

		if (hasSearch) {
			log.info("Searching students with keyword='{}'", keyword);
			students = studentService.searchStudentsSorted(keyword, page, size, sortField, sortDir);
			total = studentService.countSearchResults(keyword);
		} else {
			log.info("Fetching paginated sorted students");
			students = studentService.getStudentsPaginatedSorted(page, size, sortField, sortDir);
			total = studentService.getTotalCount();
		}

		int totalPages = (int) Math.ceil((double) total / size);

		if (totalPages > 0 && page >= totalPages) {
			log.info("Page {} out of range. Resetting to last page {}", page, totalPages - 1);
			page = totalPages - 1;

			if (hasSearch) {
				students = studentService.searchStudentsSorted(keyword, page, size, sortField, sortDir);
			} else {
				students = studentService.getStudentsPaginatedSorted(page, size, sortField, sortDir);
			}
		}

		model.addAttribute("students", students);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("size", size);
		model.addAttribute("keyword", keyword);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSort", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute(TITLE, "List Student");

		return "students/list";
	}

	/**
	 * Shows the form to create a new student.
	 *
	 * @param model Spring Model to bind empty StudentRequestDto
	 * @return create-student view
	 */
	@GetMapping("/new")
	public String showCreateForm(@RequestParam int size, @RequestParam int page, Model model) {
		log.info("Displaying create student form");
		model.addAttribute(TITLE, "Create Student");
		model.addAttribute("size", size);
		model.addAttribute("page", page);
		model.addAttribute("student", new StudentRequestDto());
		return "students/create";
	}

	/**
	 * Handles submission of the Create Student form.
	 *
	 * @param dto    StudentRequestDto with input data
	 * @param result Validation results
	 * @param ra     RedirectAttributes for success message
	 * @return redirect to student list page if success, otherwise reload form
	 */
	@PostMapping
	public String createStudent(@Valid @ModelAttribute("student") StudentRequestDto dto, BindingResult result, @RequestParam int size,
			RedirectAttributes ra) {

		log.info("Creating new student: {}", dto.getName());

		if (result.hasErrors()) {
			log.warn("Create student validation failed");
			return "students/create";
		}

		studentService.createStudent(dto);
		var total = studentService.getTotalCount();

		int page = (int) Math.ceil((double) total / size) - 1;
		ra.addFlashAttribute(SUCCESS_MESSAGE, "Student created successfully!");

		return PAGE_URL + page;
	}

	/**
	 * Shows the Edit Student form populated with existing student details.
	 *
	 * @param id    Student ID to edit
	 * @param model Model to pass existing student data
	 * @return edit-student view
	 */
	@GetMapping("/{id}/edit")
	public String showEditForm(@PathVariable Integer id, @RequestParam(defaultValue = "0") int page, @RequestParam int size, Model model) {
		log.info("Displaying edit form for student id={}", id);
		StudentResponseDto existing = studentService.getStudentById(id);

		StudentRequestDto dto = convertToRequestDto(existing);

		model.addAttribute("student", dto);
		model.addAttribute("id", id);
		model.addAttribute("page", page);
		model.addAttribute("size", size);
		model.addAttribute(TITLE, "Edit Student");

		return "students/edit";
	}

	/**
	 * Handles student update operation.
	 *
	 * @param id     Student ID to update
	 * @param dto    Updated student input
	 * @param result Validation results
	 * @param model  Model to pass data back on validation errors
	 * @param ra     RedirectAttributes for success message
	 * @return redirect to list page on success or stay on edit page if validation
	 *         fails
	 */
	@PostMapping("/{id}")
	public String updateStudent(@PathVariable Integer id, @Valid @ModelAttribute("student") StudentRequestDto dto,
			BindingResult result, @RequestParam(defaultValue = "0") int page, Model model, RedirectAttributes ra) {
		log.info("Updating student id={}", id);
		if (result.hasErrors()) {
			log.warn("Update validation failed for student id={}", id);
			model.addAttribute("id", id);
			model.addAttribute("page", page);
			model.addAttribute(TITLE, "Edit Student");
			return "students/edit";
		}

		studentService.updateStudent(id, dto);
		ra.addFlashAttribute(SUCCESS_MESSAGE, "Student updated successfully!");

		return PAGE_URL + page;
	}

	/**
	 * Deletes a student by ID.
	 *
	 * @param id Student ID to delete
	 * @param ra RedirectAttributes for success message
	 * @return redirect to student list
	 */
	@DeleteMapping("/{id}/delete")
	public String deleteStudent(@PathVariable Integer id, @RequestParam(defaultValue = "0") int page,
			RedirectAttributes ra) {
		log.info("Deleting student id={}", id);
		studentService.deleteStudent(id);
		ra.addFlashAttribute(SUCCESS_MESSAGE, "Student deleted successfully!");

		return PAGE_URL + page;
	}

	/**
	 * Returns student details in JSON format (used for View Modal).
	 *
	 * @param id Student ID
	 * @return StudentResponseDto serialized as JSON
	 */
	@GetMapping("/{id}/details")
	@ResponseBody
	public StudentResponseDto getStudentDetails(@PathVariable Integer id) {
		log.info("Fetching details for student id={}", id);
		return studentService.getStudentById(id);
	}

	private StudentRequestDto convertToRequestDto(StudentResponseDto res) {
		StudentRequestDto dto = new StudentRequestDto();

		dto.setName(res.getName());
		dto.setAddress(res.getAddress());
		dto.setGender(res.getGender());
		dto.setDob(res.getDob() != null ? LocalDate.parse(res.getDob()) : null);
		dto.setEmail(res.getEmail());
		dto.setMobile(res.getMobile());
		dto.setPhone(res.getPhone());
		return dto;
	}

}
