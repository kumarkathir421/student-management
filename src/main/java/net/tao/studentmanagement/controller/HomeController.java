package net.tao.studentmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class HomeController {

	/**
	 * Redirects root path "/" to the student listing page.
	 *
	 * @return redirect:/students
	 */
	@GetMapping("/")
	public String home() {
		log.info("Redirecting to /students/list");
		return "redirect:/students/list";
	}
}