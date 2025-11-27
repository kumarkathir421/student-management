package net.tao.studentmanagement.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

	/**
     * Handles all validation errors thrown during @Valid processing.
     * Extracts field-level error messages and forwards them to a dedicated
     * validation error page for user-friendly display.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationErrors(MethodArgumentNotValidException ex, Model model) {

        Map<String, String> errors = new HashMap<>();

        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }

        model.addAttribute("validationErrors", errors);
        return "error/validation-error"; 
    }

    /**
     * Handles cases where a requested student/resource is not found.
     * Adds the error message to the model and displays a simple 'not found' page.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error/not-found"; 
    }

    /**
     * Catches any unexpected system-level exception that isn't handled
     * by other handlers. Displays a generic error page to prevent exposing
     * internal details.
     */
    @ExceptionHandler(Exception.class)
    public String handleGlobalException(Exception ex, Model model) {
        model.addAttribute("message", "Something went wrong: " + ex.getMessage());
        return "error/general-error"; 
    }
    
    /**
     * Handles runtime exceptions by redirecting back to the student list page.
     * The error message is shown as a toast alert using RedirectAttributes.
     */
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, RedirectAttributes ra) {
        ra.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/students";
    }

}
