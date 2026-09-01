# SQL Script for Predefined Users

## Overview
The `predefined_users.sql` script creates sample data for the University Equipment Lending System. Since the system does not allow registration, this script predefines all necessary users and related data.

## What the Script Does

1. **Cleans Existing Data** (optional)
   - Removes any existing records from related tables
   - Resets ID sequences

2. **Creates User Accounts**
   - 1 Administrator
   - 2 Academic Staff members
   - 2 Professional Staff members
   - 3 Students

3. **Creates Sample Data**
   - 2 Courses
   - Student course enrollments
   - 3 Equipment items
   - 3 Lending records (2 returned, 1 borrowed)

## User Credentials

### Administrator
- Username: `admin1`
- Password: `admin123`

### Academic Staff
- Username: `prof.smith`
- Password: `prof123`

- Username: `prof.johnson`
- Password: `prof456`

### Professional Staff
- Username: `tech.support`
- Password: `tech123`

- Username: `lab.manager`
- Password: `lab123`

### Students
- Username: `student1`
- Password: `student123`

- Username: `student2`
- Password: `student456`

- Username: `student3`
- Password: `student789`

## How to Use

1. Connect to your PostgreSQL database using a client like pgAdmin or the command line.
2. Execute the script:
   ```
   psql -U your_username -d your_database -f predefined_users.sql
   ```
   
   Or in SQL Server Management Studio:
   - Open the script file
   - Connect to your database
   - Execute the script

3. If you want to keep existing data, comment out the DELETE statements at the beginning of the script.

## Notes

- The script is designed to work with the database schema defined in the application.