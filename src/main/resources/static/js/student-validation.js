$(document).ready(function() {

	$("#studentForm").submit(function(e) {
		let firstInvalidField = null;

		$(".error-text").text("");

		firstInvalidField ||= validateRequired("#name", "#nameError", "Name is required");
		firstInvalidField ||= validateDob();
		firstInvalidField ||= validateEmail();
		firstInvalidField ||= validateMaxLength("#mobile", 15, "#mobileError", "Mobile cannot exceed 15 digits");
		firstInvalidField ||= validateMaxLength("#phone", 15, "#phoneError", "Phone cannot exceed 15 digits");

		if (firstInvalidField) {
			const element = firstInvalidField[0];

			element.scrollIntoView({ behavior: "smooth", block: "center" });
			setTimeout(() => element.focus(), 150);

			e.preventDefault();
		}
	});

	function validateRequired(fieldId, errorId, message) {
		const field = $(fieldId);
		if (field.val().trim().length === 0) {
			$(errorId).text(message);
			return field;
		}
		return null;
	}

	function validateDob() {
		const field = $("#dob");
		const dob = field.val();
		if (!dob) return null;

		const date = new Date(dob);
		const today = new Date();

		if (date >= today) {
			$("#dobError").text("DOB must be in the past");
			return field;
		}
		return null;
	}

	function validateEmail() {
		const field = $("#email");
		const email = field.val().trim();
		if (email.length === 0) return null;

		const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

		if (!emailRegex.test(email)) {
			$("#emailError").text("Invalid email format");
			return field;
		}
		return null;
	}

	function validateMaxLength(fieldId, max, errorId, message) {
		const field = $(fieldId);
		if (field.val().trim().length > max) {
			$(errorId).text(message);
			return field;
		}
		return null;
	}

});
