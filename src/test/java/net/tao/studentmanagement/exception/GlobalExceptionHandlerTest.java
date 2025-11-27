package net.tao.studentmanagement.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.lang.reflect.Method;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void whenResourceNotFound_thenReturnsNotFoundView() throws Exception {
        mockMvc.perform(get("/test/notfound"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/not-found"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message", "Student not found with id: 123"));
    }

    @Test
    void whenValidationError_thenReturnsValidationErrorView() throws Exception {
        mockMvc.perform(get("/test/validation"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/validation-error"))
                .andExpect(model().attributeExists("validationErrors"))
                .andExpect(result -> {
                    @SuppressWarnings("unchecked")
                    Map<String, String> errors =
                            (Map<String, String>) result.getModelAndView().getModel().get("validationErrors");
                    assertThat(errors).containsEntry("name", "Name is required");
                });
    }

    @Test
    void whenRuntimeException_thenRedirectsToStudentsWithFlash() throws Exception {
        mockMvc.perform(get("/test/runtime"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/students"))
                .andExpect(flash().attributeExists("errorMessage"))
                .andExpect(flash().attribute("errorMessage", "Runtime Exception"));
    }

    @Test
    void whenGenericException_thenReturnsGeneralErrorView() throws Exception {
        mockMvc.perform(get("/test/generic"))
                .andExpect(status().isOk())
                .andExpect(view().name("error/general-error"))
                .andExpect(model().attributeExists("message"))
                .andExpect(model().attribute("message",
                        "Something went wrong: generic error"));
    }

    @Controller
    private static class TestController {

        @GetMapping("/test/notfound")
        public void throwNotFound() {
            throw new ResourceNotFoundException("Student not found with id: 123");
        }

        @GetMapping("/test/validation")
        public void throwValidation() throws Exception {

            BindingResult br = new BeanPropertyBindingResult(new Object(), "student");
            br.addError(new FieldError("student", "name", "Name is required"));

            Method m = this.getClass().getDeclaredMethod("constructorMethod");
            MethodParameter mp = new MethodParameter(m, -1);

            throw new MethodArgumentNotValidException(mp, br);
        }

        @GetMapping("/test/runtime")
        public void throwRuntime() {
            throw new RuntimeException("Runtime Exception");
        }

        @GetMapping("/test/generic")
        public void throwGeneric() throws Exception {
            throw new Exception("generic exception");
        }

        @SuppressWarnings("unused")
		public void constructorMethod() {
        	// used to create a MethodParameter for MethodArgumentNotValidException.
        }
    }
}
