package net.tao.studentmanagement.mapper;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import net.tao.studentmanagement.dto.StudentRequestDto;
import net.tao.studentmanagement.dto.StudentResponseDto;
import net.tao.studentmanagement.model.Student;

@Mapper(componentModel = "spring")
public interface StudentMapper {

	 /**
     * Converts the StudentRequestDto (input from UI/API)
     * into a Student entity for INSERT operations.
     *
     * - Ignores ID because it is auto-generated.
     * - Converts LocalDate → LocalDateTime for DB storage.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dob", expression = "java(convertToDateTime(dto.getDob()))")
    Student toEntity(StudentRequestDto dto);

    /**
     * Converts a Student entity into a StudentResponseDto
     * for UI display and API responses.
     *
     * - Converts LocalDateTime → yyyy-MM-dd string
     *   (to match <input type="date"> format).
     */
    @Mapping(target = "dob", expression = "java(formatDob(entity.getDob()))")
    StudentResponseDto toResponseDto(Student entity);

    /**
     * Updates an existing Student entity using the non-null fields
     * of StudentRequestDto.
     *
     * - Performs PATCH-style update: null values are ignored.
     * - Prevents ID overwrite by ignoring it.
     * - Converts LocalDate → LocalDateTime.
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true) 
    @Mapping(target = "dob", expression = "java(convertToDateTime(dto.getDob()))")
    void updateEntityFromDto(StudentRequestDto dto, @MappingTarget Student entity);

    /**
     * Converts LocalDate from UI to LocalDateTime for DB column.
     * Stored as 00:00:00 since only date is relevant.
     */
    default LocalDateTime convertToDateTime(LocalDate dob) {
        if (dob == null) return null;
        return LocalDateTime.parse(dob + "T00:00:00");
    }

    /**
     * Formats LocalDateTime from DB into a yyyy-MM-dd string
     * suitable for Thymeleaf date input fields.
     */
    default String formatDob(LocalDateTime dob) {
        if (dob == null) return null;
        return dob.toLocalDate().toString();
    }
}
