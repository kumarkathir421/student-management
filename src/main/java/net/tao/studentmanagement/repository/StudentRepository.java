package net.tao.studentmanagement.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.tao.studentmanagement.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
	List<Student> findByNameContainingIgnoreCase(String name, Pageable pageable);
	long countByNameContainingIgnoreCase(String name);

}
