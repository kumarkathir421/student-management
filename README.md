# Student Management System

Student Management CRUD Application

A Web application built using Spring MVC, Spring Data JPA, Thymeleaf, and jQuery.  
Supports:

- Student registration  
- Edit, delete, view details (AJAX modal)  
- Pagination  
- Search  
- Client-side + server-side validations  
- Clean layered architecture  
- MapStruct-based DTO mapping
- Centralized Exception Handling
- Code Coverage (Jacoco integrated)


## Tech Stack

### **Backend**
- Java 17  
- Spring Boot 3  
- Spring MVC  
- Spring Data JPA  
- Hibernate  
- MapStruct  
- Maven  

### **Frontend**
- Thymeleaf  
- Bootstrap 5  
- jQuery  

### **Database**
- H2 (for demo)
- Customizable to MySQL / PostgreSQL (for DB)

### Project Architecture
 - controller → service → repository → model → dto → mapper → exception

### CMD commands to run the application and report ###
 - mvn clean test install
 - java -jar target/student-management-0.0.1-SNAPSHOT.jar
 - mvn spring-boot:run
 
### Jacoco Report
 - target/site/jacoco/index.html
 
### Application url
 - http://localhost:8080/
 
### H2 Console
 - http://localhost:8080/h2-console
 - username: sa
 - password: 
 - url: jdbc:h2:mem:studentdb



