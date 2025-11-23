package net.tao.studentmanagement.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import net.tao.studentmanagement.dto.StudentRequestDto;
import net.tao.studentmanagement.dto.StudentResponseDto;
import net.tao.studentmanagement.model.Student;

class StudentMapperTest {

    private final StudentMapper mapper = Mappers.getMapper(StudentMapper.class);

    @Test
    void testToEntity() {
        StudentRequestDto dto = new StudentRequestDto();
        dto.setName("John");
        dto.setDob(LocalDate.of(2000, 1, 5));

        Student entity = mapper.toEntity(dto);

        assertNull(entity.getId());
        assertEquals("John", entity.getName());
        assertEquals(LocalDateTime.of(2000, 1, 5, 0, 0), entity.getDob());
    }

    @Test
    void testToResponseDto() {
        Student entity = new Student();
        entity.setId(10);
        entity.setName("Alice");
        entity.setDob(LocalDateTime.of(1998, 12, 25, 0, 0));

        StudentResponseDto dto = mapper.toResponseDto(entity);

        assertEquals(10, dto.getId());
        assertEquals("Alice", dto.getName());
        assertEquals("1998-12-25", dto.getDob()); // formatted yyyy-MM-dd
    }

    @Test
    void testUpdateEntityFromDto() {
        StudentRequestDto dto = new StudentRequestDto();
        dto.setName("Updated Name");
        dto.setDob(LocalDate.of(1995, 6, 15));

        Student entity = new Student();
        entity.setId(99);
        entity.setName("Old Name");
        entity.setDob(LocalDateTime.of(1990, 1, 1, 0, 0));

        mapper.updateEntityFromDto(dto, entity);

        assertEquals(99, entity.getId());
        assertEquals("Updated Name", entity.getName());
        assertEquals(LocalDateTime.of(1995, 6, 15, 0, 0), entity.getDob());
    }

    @Test
    void testConvertToDateTime() {
        LocalDate date = LocalDate.of(2020, 2, 2);

        LocalDateTime result = mapper.convertToDateTime(date);

        assertEquals(LocalDateTime.of(2020, 2, 2, 0, 0), result);
    }

    @Test
    void testConvertToDateTime_null() {
        assertNull(mapper.convertToDateTime(null));
    }

    @Test
    void testFormatDob() {
        LocalDateTime dateTime = LocalDateTime.of(2021, 3, 3, 0, 0);

        String result = mapper.formatDob(dateTime);

        assertEquals("2021-03-03", result);
    }

    @Test
    void testFormatDob_null() {
        assertNull(mapper.formatDob(null));
    }
    
    @Test
    void testUpdateEntityFromDto_ignoreNulls() {
        StudentRequestDto dto = new StudentRequestDto();
        dto.setName(null);
        dto.setDob(null);

        Student entity = new Student();
        entity.setId(50);
        entity.setName("Original");
        entity.setDob(LocalDateTime.of(2000, 1, 1, 0, 0));

        mapper.updateEntityFromDto(dto, entity);

        assertEquals(50, entity.getId());
        assertEquals("Original", entity.getName());
        
        assertNull(entity.getDob());
    }

    @Test
    void testToEntity_nullDob() {
        StudentRequestDto dto = new StudentRequestDto();
        dto.setName("John");
        dto.setDob(null);

        Student entity = mapper.toEntity(dto);

        assertNull(entity.getDob());
        assertEquals("John", entity.getName());
    }

    
}
