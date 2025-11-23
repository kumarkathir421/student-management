package net.tao.studentmanagement.service.impl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.List;

import net.tao.studentmanagement.dto.StudentRequestDto;
import net.tao.studentmanagement.dto.StudentResponseDto;
import net.tao.studentmanagement.exception.ResourceNotFoundException;
import net.tao.studentmanagement.mapper.StudentMapper;
import net.tao.studentmanagement.model.Student;
import net.tao.studentmanagement.repository.StudentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

class StudentServiceImplTest {

    @Mock
    private StudentRepository repo;

    @Mock
    private StudentMapper mapper;

    @InjectMocks
    private StudentServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateStudent() {
        StudentRequestDto req = new StudentRequestDto();
        req.setName("John");

        Student entity = new Student();
        entity.setId(1);
        entity.setName("John");

        StudentResponseDto res = new StudentResponseDto();
        res.setId(1);
        res.setName("John");

        when(mapper.toEntity(req)).thenReturn(entity);
        when(repo.save(entity)).thenReturn(entity);
        when(mapper.toResponseDto(entity)).thenReturn(res);

        StudentResponseDto output = service.createStudent(req);

        assertEquals(1, output.getId());
        assertEquals("John", output.getName());
    }

    @Test
    void testUpdateStudent_success() {
        StudentRequestDto req = new StudentRequestDto();
        req.setName("Updated");

        Student entity = new Student();
        entity.setId(1);

        StudentResponseDto dto = new StudentResponseDto();
        dto.setId(1);

        when(repo.findById(1)).thenReturn(Optional.of(entity));
        when(repo.save(entity)).thenReturn(entity);
        when(mapper.toResponseDto(entity)).thenReturn(dto);

        StudentResponseDto result = service.updateStudent(1, req);

        verify(mapper).updateEntityFromDto(req, entity);
        verify(repo).save(entity);
        assertEquals(1, result.getId());
    }

    @Test
    void testUpdateStudent_notFound() {
    	when(repo.findById(100)).thenReturn(Optional.empty());
        StudentRequestDto dto = new StudentRequestDto();
        assertThrows(ResourceNotFoundException.class,
                () -> service.updateStudent(100, dto));
    }

    @Test
    void testGetStudentById_success() {
        Student entity = new Student();
        entity.setId(10);

        StudentResponseDto res = new StudentResponseDto();
        res.setId(10);

        when(repo.findById(10)).thenReturn(Optional.of(entity));
        when(mapper.toResponseDto(entity)).thenReturn(res);

        StudentResponseDto dto = service.getStudentById(10);

        assertEquals(10, dto.getId());
    }

    @Test
    void testGetStudentById_notFound() {
        when(repo.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getStudentById(99));
    }

    @Test
    void testGetAllStudents() {
        Student s = new Student();
        StudentResponseDto dto = new StudentResponseDto();

        when(repo.findAll()).thenReturn(List.of(s));
        when(mapper.toResponseDto(s)).thenReturn(dto);

        List<StudentResponseDto> result = service.getAllStudents();

        assertEquals(1, result.size());
    }

    @Test
    void testDeleteStudent_success() {
        when(repo.existsById(1)).thenReturn(true);
        doNothing().when(repo).deleteById(1);

        service.deleteStudent(1);
        verify(repo, times(1)).deleteById(1);
    }

    @Test
    void testDeleteStudent_notFound() {
        when(repo.existsById(5)).thenReturn(false);
        assertThrows(ResourceNotFoundException.class, () -> service.deleteStudent(5));
    }

    @Test
    void testGetTotalCount() {
        when(repo.count()).thenReturn(7L);

        long count = service.getTotalCount();

        assertEquals(7, count);
    }

    @Test
    void testSearchStudents() {
        Student s = new Student();
        StudentResponseDto dto = new StudentResponseDto();

        when(repo.findByNameContainingIgnoreCase(eq("john"), any(Pageable.class)))
                .thenReturn(List.of(s));

        when(mapper.toResponseDto(s)).thenReturn(dto);

        List<StudentResponseDto> list = service.searchStudents("john", 0, 10);

        assertEquals(1, list.size());
    }

    @Test
    void testCountSearchResults() {
        when(repo.countByNameContainingIgnoreCase("john")).thenReturn(3L);

        long count = service.countSearchResults("john");

        assertEquals(3, count);
    }

    @Test
    void testGetStudentsPaginatedSorted() {
        Student student = new Student();
        student.setId(1);

        StudentResponseDto dto = new StudentResponseDto();
        dto.setId(1);

        when(repo.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(student)));

        when(mapper.toResponseDto(student)).thenReturn(dto);

        List<StudentResponseDto> list =
                service.getStudentsPaginatedSorted(0, 5, "id", "asc");

        assertEquals(1, list.size());
    }

    @Test
    void testSearchStudentsSorted() {
        Student student = new Student();
        StudentResponseDto dto = new StudentResponseDto();

        when(repo.findByNameContainingIgnoreCase(eq("john"), any(Pageable.class)))
                .thenReturn(List.of(student));

        when(mapper.toResponseDto(student)).thenReturn(dto);

        List<StudentResponseDto> list =
                service.searchStudentsSorted("john", 0, 5, "name", "desc");

        assertEquals(1, list.size());
    }
    
    @Test
    void testGetStudentsPaginatedSorted_desc() {
        Student student = new Student();
        student.setId(2);

        StudentResponseDto dto = new StudentResponseDto();
        dto.setId(2);

        when(repo.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(student)));

        when(mapper.toResponseDto(student)).thenReturn(dto);

        List<StudentResponseDto> list =
                service.getStudentsPaginatedSorted(0, 5, "id", "desc");

        assertEquals(1, list.size());
    }

    @Test
    void testSearchStudentsSorted_asc() {
        Student student = new Student();
        StudentResponseDto dto = new StudentResponseDto();

        when(repo.findByNameContainingIgnoreCase(eq("john"), any(Pageable.class)))
                .thenReturn(List.of(student));

        when(mapper.toResponseDto(student)).thenReturn(dto);

        List<StudentResponseDto> list =
                service.searchStudentsSorted("john", 0, 5, "name", "asc");

        assertEquals(1, list.size());
    }
    
    @Test
    void testSearchStudentsSorted_desc() {
        Student student = new Student();
        StudentResponseDto dto = new StudentResponseDto();

        when(repo.findByNameContainingIgnoreCase(eq("john"), any(Pageable.class)))
                .thenReturn(List.of(student));

        when(mapper.toResponseDto(student)).thenReturn(dto);

        List<StudentResponseDto> list =
                service.searchStudentsSorted("john", 0, 5, "name", "desc");

        assertEquals(1, list.size());
    }

    @Test
    void testSearchStudents_empty() {
        when(repo.findByNameContainingIgnoreCase(eq("none"), any(Pageable.class)))
                .thenReturn(List.of());

        List<StudentResponseDto> list = service.searchStudents("none", 0, 10);

        assertTrue(list.isEmpty());
    }

    @Test
    void testGetStudentsPaginatedSorted_empty() {
        when(repo.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        List<StudentResponseDto> list =
                service.getStudentsPaginatedSorted(0, 10, "id", "asc");

        assertTrue(list.isEmpty());
    }

    @Test
    void testSearchStudentsSorted_empty() {
        when(repo.findByNameContainingIgnoreCase(eq("none"), any(Pageable.class)))
                .thenReturn(List.of());

        List<StudentResponseDto> list =
                service.searchStudentsSorted("none", 0, 5, "id", "asc");

        assertTrue(list.isEmpty());
    }

    @Test
    void testDeleteStudent_notFound_noDeleteCall() {
        when(repo.existsById(5)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.deleteStudent(5));

        verify(repo, never()).deleteById(anyInt());
    }

}
