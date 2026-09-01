-- SQL Script to create predefined users for the University Equipment Lending System
-- This script inserts data into the users table and the specific user type tables

-- Clear existing data (optional, comment out if you want to keep existing data)
DELETE FROM student_courses;
DELETE FROM lending_records;
DELETE FROM students;
DELETE FROM academic_staff;
DELETE FROM professional_staff;
DELETE FROM administrators;
DELETE FROM users;

-- Reset sequences
ALTER SEQUENCE users_id_seq RESTART WITH 1;

-- Insert Administrators
INSERT INTO users (username, password, first_name, last_name, email, phone_number, date_of_birth, address, user_type)
VALUES 
('admin1', 'admin123', 'John', 'Admin', 'john.admin@university.edu', '123-456-7890', '1980-01-15', '123 Admin St, Admin City', 'Administrator');

INSERT INTO administrators (user_id, admin_id, department, position, access_level)
VALUES 
(1, 'ADM001', 'IT Department', 'System Administrator', 'Full');

-- Insert Academic Staff
INSERT INTO users (username, password, first_name, last_name, email, phone_number, date_of_birth, address, user_type)
VALUES 
('prof.smith', 'prof123', 'Robert', 'Smith', 'robert.smith@university.edu', '234-567-8901', '1975-05-20', '456 Faculty Ave, University City', 'AcademicStaff'),
('prof.johnson', 'prof456', 'Emily', 'Johnson', 'emily.johnson@university.edu', '345-678-9012', '1982-08-12', '789 Professor Ln, College Town', 'AcademicStaff');

INSERT INTO academic_staff (user_id, staff_id, department, position)
VALUES 
(2, 'ACAD001', 'Computer Science', 'Professor'),
(3, 'ACAD002', 'Engineering', 'Associate Professor');

-- Insert Professional Staff
INSERT INTO users (username, password, first_name, last_name, email, phone_number, date_of_birth, address, user_type)
VALUES 
('tech.support', 'tech123', 'Michael', 'Tech', 'michael.tech@university.edu', '456-789-0123', '1988-11-30', '101 Support Rd, Tech Village', 'ProfessionalStaff'),
('lab.manager', 'lab123', 'Sarah', 'Manager', 'sarah.manager@university.edu', '567-890-1234', '1985-04-25', '202 Lab Blvd, Research Park', 'ProfessionalStaff');

INSERT INTO professional_staff (user_id, staff_id, department, position, specialization)
VALUES 
(4, 'PROF001', 'IT Support', 'Technical Support Specialist', 'Hardware Troubleshooting'),
(5, 'PROF002', 'Engineering', 'Lab Manager', 'Equipment Maintenance');

-- Insert Students
INSERT INTO users (username, password, first_name, last_name, email, phone_number, date_of_birth, address, user_type)
VALUES 
('student1', 'student123', 'Alex', 'Student', 'alex.student@university.edu', '678-901-2345', '2000-03-10', '303 Dorm St, Campus Area', 'Student'),
('student2', 'student456', 'Jessica', 'Learner', 'jessica.learner@university.edu', '789-012-3456', '2001-07-22', '404 College Dr, University Heights', 'Student'),
('student3', 'student789', 'David', 'Scholar', 'david.scholar@university.edu', '890-123-4567', '1999-12-05', '505 Study Ln, Knowledge Park', 'Student');

INSERT INTO students (user_id, student_id, major, year)
VALUES 
(6, 'S10001', 'Computer Science', 3),
(7, 'S10002', 'Electrical Engineering', 2),
(8, 'S10003', 'Mechanical Engineering', 4);

-- Insert Courses (for reference in student enrollments)
INSERT INTO courses (course_code, course_name, description, semester, year, instructor_id)
VALUES 
('CS101', 'Introduction to Programming', 'Basic programming concepts using Java', 1, 2023, 2),
('ENG201', 'Circuit Analysis', 'Fundamentals of electrical circuits', 1, 2023, 3);

-- Enroll students in courses
INSERT INTO student_courses (student_id, course_id)
VALUES 
(6, 1),  -- Alex in CS101
(7, 2),  -- Jessica in ENG201
(8, 1),  -- David in CS101
(8, 2);  -- David in ENG201

-- Insert some equipment for testing
INSERT INTO equipment (name, description, category, condition, purchase_date, purchase_price, manufacturer, model, serial_number, location, available)
VALUES 
('Laptop Dell XPS', 'High-performance laptop for development', 'Computer', 'Excellent', '2022-01-15', 1200.00, 'Dell', 'XPS 15', 'DELL123456', 'IT Lab Room 101', TRUE),
('Oscilloscope', 'Digital oscilloscope for circuit testing', 'Test Equipment', 'Good', '2021-06-20', 800.00, 'Tektronix', 'TBS1052B', 'TEK789012', 'Engineering Lab Room 203', TRUE),
('3D Printer', 'Professional grade 3D printer', 'Manufacturing', 'Excellent', '2022-03-10', 2500.00, 'Ultimaker', 'S5', 'ULT345678', 'Design Lab Room 305', TRUE);

-- Insert some lending records for history
INSERT INTO lending_records (borrower_id, equipment_id, course_id, borrow_date, due_date, return_date, status, purpose, condition, notes, approver_id)
VALUES 
(6, 1, 1, '2023-02-10 09:00:00', '2023-02-17 17:00:00', '2023-02-16 15:30:00', 'Returned', 'Programming project', 'Excellent', 'Returned on time', 2),
(7, 2, 2, '2023-03-05 10:00:00', '2023-03-12 17:00:00', '2023-03-11 14:00:00', 'Returned', 'Circuit lab work', 'Good', 'No issues', 3),
(8, 3, 1, '2023-04-01 11:00:00', '2023-04-08 17:00:00', NULL, 'Borrowed', 'Design project', 'Excellent', 'Extended for project completion', 2);