package net.tao.studentmanagement.controller;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import net.tao.studentmanagement.dto.StudentResponseDto;
import net.tao.studentmanagement.service.StudentService;

@WebMvcTest(StudentController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private StudentService service;

    @Test
    void testGetStudentsList() throws Exception {
        mockMvc.perform(get("/students/list"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/list"));
    }

    @Test
    void testShowCreateForm() throws Exception {
        mockMvc.perform(get("/students/new")
        		.param("size", "10").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/create"))
                .andExpect(model().attributeExists("student"));
    }

    @Test
    void testCreateStudent_validationError() throws Exception {
        mockMvc.perform(post("/students")
        		.param("size", "10")
                .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("students/create"));
    }

    @Test
    void testEditPageLoads() throws Exception {
        StudentResponseDto dto = new StudentResponseDto();
        dto.setId(1);
        dto.setName("John");
        dto.setDob("2000-01-01");

        when(service.getStudentById(1)).thenReturn(dto);

        mockMvc.perform(get("/students/1/edit").param("size", "10").param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/edit"))
                .andExpect(model().attributeExists("student"));
    }

    @Test
    void testDtoMapping_withNullDob() throws Exception {
        StudentResponseDto res = new StudentResponseDto();
        res.setId(1);
        res.setName("John");
        res.setDob(null);

        when(service.getStudentById(1)).thenReturn(res);
        
        mockMvc.perform(get("/students/1/edit").param("size", "10").param("page", "0"))
        .andExpect(status().isOk())
        .andExpect(view().name("students/edit"))
        .andExpect(model().attributeExists("student"));
    }

    @Test
    void testDeleteStudent_success() throws Exception {
        mockMvc.perform(delete("/students/5/delete")
                .param("page", "0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students/list?page=0"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void testGetStudentDetails() throws Exception {
        StudentResponseDto dto = new StudentResponseDto();
        dto.setId(1);
        dto.setName("John");

        when(service.getStudentById(1)).thenReturn(dto);

        mockMvc.perform(get("/students/1/details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"));
    }
    
    @SuppressWarnings("unchecked")
	@Test
    void testStudentsList_withSearchAsc() throws Exception {
        List<StudentResponseDto> list = List.of(new StudentResponseDto());

        when(service.searchStudentsSorted(eq("john"), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(list, list);

        when(service.countSearchResults("john"))
                .thenReturn(1L);

        mockMvc.perform(get("/students/list")
                .param("keyword", "john")
                .param("page", "0")
                .param("size", "10")
                .param("sortField", "id")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/list"))
                .andExpect(model().attributeExists("students"));
    }
    
    @SuppressWarnings("unchecked")
	@Test
    void testStudentsList_withSearchDesc() throws Exception {
        List<StudentResponseDto> list = List.of(new StudentResponseDto());

        when(service.searchStudentsSorted(eq("john"), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(list, list);

        when(service.countSearchResults("john"))
                .thenReturn(1L);

        mockMvc.perform(get("/students/list")
                .param("keyword", "john")
                .param("page", "0")
                .param("size", "10")
                .param("sortField", "id")
                .param("sortDir", "desc"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/list"))
                .andExpect(model().attributeExists("students"));
    }
    
    @SuppressWarnings("unchecked")
	@Test
    void testStudentsList_paginated() throws Exception {
        List<StudentResponseDto> list = List.of(new StudentResponseDto());

        when(service.getTotalCount()).thenReturn(5L);

        when(service.getStudentsPaginatedSorted(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(list, list);

        mockMvc.perform(get("/students/list")
                .param("page", "0")
                .param("size", "10")
                .param("sortField", "id")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/list"))
                .andExpect(model().attributeExists("students"));
    }

    @SuppressWarnings("unchecked")
	@Test
    void testStudentsList_pageReset() throws Exception {
        List<StudentResponseDto> list = List.of(new StudentResponseDto());

        when(service.getTotalCount()).thenReturn(10L);

        when(service.getStudentsPaginatedSorted(anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(list, list);

        mockMvc.perform(get("/students/list")
                .param("page", "10")
                .param("size", "10")
                .param("sortField", "id")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/list"))
                .andExpect(model().attributeExists("students"));
    }

    @Test
    void testUpdateStudent_validationError() throws Exception {
        mockMvc.perform(post("/students/1")
                .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("students/edit"))
                .andExpect(model().attributeExists("id"))
                .andExpect(model().attributeExists("page"));
    }

    @Test
    void testListStudents_negativePage_resetsToZero() throws Exception {
        mockMvc.perform(get("/students/list")
                .param("page", "-5"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/list"))
                .andExpect(model().attribute("currentPage", 0));
    }
    
    @Test
    void testListStudents_reverseSortAttribute() throws Exception {
        mockMvc.perform(get("/students/list")
                .param("sortField", "name")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("reverseSort", "desc"));
    }

    @SuppressWarnings("unchecked")
	@Test
    void testStudentsList_pageReset_withSearch() throws Exception {
        List<StudentResponseDto> list = List.of(new StudentResponseDto());

        when(service.countSearchResults("aaa")).thenReturn(5L);
        when(service.searchStudentsSorted(eq("aaa"), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(list, list);

        mockMvc.perform(get("/students/list")
                .param("keyword", "aaa")
                .param("page", "10")
                .param("size", "10")
                .param("sortField", "id")
                .param("sortDir", "asc"))
                .andExpect(status().isOk())
                .andExpect(view().name("students/list"))
                .andExpect(model().attributeExists("students"));
    }

    @Test
    void testCreateStudent_addsFlashMessage() throws Exception {
        mockMvc.perform(post("/students")
                .param("name", "John")
                .param("gender", "M")
                .param("size", "10"))
                .andExpect(flash().attributeExists("successMessage"));
    }

    @Test
    void testUpdateStudent_addsFlashMessage() throws Exception {
        mockMvc.perform(post("/students/1")
        		.param("size", "10")
        		.param("page", "0")
                .param("name", "Test")
                .param("gender", "M"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("successMessage"));
    }

}
