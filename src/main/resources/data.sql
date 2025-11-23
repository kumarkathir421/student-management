-- DDL
-- STUDENT TABLE

CREATE TABLE IF NOT EXISTS Student (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(45) NOT NULL,
    address VARCHAR(45),
    gender CHAR(1) DEFAULT 'M',
    dob TIMESTAMP,
    email VARCHAR(45),
    mobile VARCHAR(15),
    phone VARCHAR(15)
);


-- DML
-- STUDENT SAMPLE DATA

INSERT INTO Student (name, address, gender, dob, email, mobile, phone) VALUES
('Alice', 'Texas', 'M', '1990-05-15 00:00:00', 'alice@test.com', '076543210', '044123456'),
('Elisa', 'Washington', 'F', '1993-10-02 00:00:00', 'bob@test.com', '0123456789', NULL);

