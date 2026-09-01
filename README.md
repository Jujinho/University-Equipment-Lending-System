# University Equipment Lending System

## Overview
The University Equipment Lending System is a comprehensive application for managing equipment lending in a university setting. The system supports multiple user roles (visitor, student, academic staff, professional staff, and administrator) with different permissions and functionalities.

Github repository link: https://github.com/RMIT-Vietnam-Teaching/further-programming-assignment-2-build-a-backend-tean9_sgs
Video link: https://youtu.be/jI_BeOzH1NY
## Features
- **User Management**: Create, update, and delete users with different roles (student, academic staff, professional staff, administrator)
- **Equipment Management**: Add, update, and delete equipment with images
- **Course Management**: Create courses, assign academic staff, and enroll students
- **Lending Management**: Borrow and return equipment, approve and reject lending requests
- **Notification System**: Send notifications for lending requests, approvals, rejections, and overdue equipment
- **Statistics**: View lending statistics for academic staff and the entire system

## User Roles
- **Visitor**: View available equipment with filtering options
- **Student**: View and update personal information, borrow and return equipment (with approval), view lending history
- **Academic Staff**: Manage personal information, approve student borrowing requests, borrow equipment, view statistics
- **Professional Staff**: Manage personal information, borrow equipment, view lending history
- **Administrator**: Full access to all functionalities, including CRUD operations on all entities

## Technical Details
- **Programming Language**: Java
- **GUI Framework**: JavaFX
- **Database**: PostgreSQL
- **Build Tool**: Maven
- **Testing Framework**: JUnit 5

## Project Structure
- `src/main/java/org/example/model`: Model classes (User, Student, AcademicStaff, ProfessionalStaff, Administrator, Equipment, Course, LendingRecord)
- `src/main/java/org/example/db`: Database connection and repository classes
- `src/main/java/org/example/service`: Service classes for business logic
- `src/main/java/org/example/controller`: JavaFX controllers
- `src/main/resources/fxml`: JavaFX FXML files
- `src/test/java/org/example`: Unit tests

## Setup Instructions

### Prerequisites
- Java Development Kit (JDK) 17 or newer
- Maven 3.6.0 or newer
- JavaFX SDK 17 or newer (download from [openjfx.io](https://gluonhq.com/products/javafx/))
- PostgreSQL database server

### Installation Steps
1. Clone the repository
2. Download and extract JavaFX SDK to a permanent location on your computer
3. (Optional) Configure database connection by setting environment variables as described in the Database Setup section

### JavaFX Environment Setup
Set up a PATH_TO_FX environment variable (optional but recommended):

#### Windows (Command Prompt)
```cmd
set PATH_TO_FX=C:\path\to\javafx-sdk\lib
```

#### macOS (Terminal - Bash)
```bash
export PATH_TO_FX=/path/to/javafx-sdk/lib
```

To make it permanent on macOS, add the export line to your `~/.bash_profile` file.

#### Building and Running
1. Build with tests (recommended):
```
mvn clean package
```

2. Build without tests (if you encounter test failures):
```
mvn clean package -DskipTests
```

3. Run using Maven (easiest method):
```
mvn javafx:run
```

4. Run using Java with direct module path:

Windows:
```
java --module-path "C:\path\to\javafx-sdk\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar target\Assignmen-2-1.0-SNAPSHOT.jar
```

macOS:
```
java --module-path "/path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar target/Assignmen-2-1.0-SNAPSHOT.jar
```
 On some versions of macOS, JavaFX apps may crash due to native rendering bugs.
 The -Dprism.order=sw flag forces software rendering to avoid such issues.
```
java 
--module-path "/path/to/javafx-sdk/lib" 
--add-modules javafx.controls,javafx.fxml 
-Dprism.order=sw 
-jar target/Assignment-2-1.0-SNAPSHOT.jar

```
5. Run using Java with environment variable:

Windows (Command Prompt):
```cmd
java --module-path %PATH_TO_FX% --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar target\Assignmen-2-1.0-SNAPSHOT.jar
```

macOS (Bash):
```bash
java --module-path "$PATH_TO_FX" --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar target/Assignmen-2-1.0-SNAPSHOT.jar
```


## Database Setup
The application requires a PostgreSQL database. The database schema is automatically created when the application starts. The following database connection details are already configured in the `DatabaseConnection` class:
- Database URL: `jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:6543/postgres`
- Username: `postgres.weaplidrrnnmjwkdpglw`
- Password: `RMIT@2025yta`

### Predefined Users
The system comes with predefined users since registration is not allowed. The SQL script to create these users is located at:
- `src/main/resources/predefined_users.sql`

Documentation for the SQL script is available at:
- `src/main/resources/README_SQL_SCRIPT.md`

To use the script, connect to your database and execute the SQL file. The script creates various user types (administrator, academic staff, professional staff, students) with predefined credentials.

## Testing
The application includes unit tests for the model classes, services, and controllers. To run all tests:
```
mvn test
```

### Fixing Common Test Issues

#### HikariCP Connection Pool Errors
If you encounter errors related to closed HikariCP connection pools in tests:
```
Test setup failed: HikariDataSource HikariDataSource (HikariPool-1) has been closed
```

Make sure that your database connection is properly configured and the test classes properly close the connection pool after use.

#### Mock Verification Failures
For controller test failures related to mock interactions:
```
Wanted but not invoked: equipmentService.getAllEquipment();
```

These can be fixed by updating the test to match the current controller implementation or by modifying the controller to match the expected behavior in the test.

## Building the JAR File
To build the JAR file, use the following command:
```
mvn clean package
```
This creates a JAR file with dependencies in the `target` directory.

### Important: Running the JAR File
The generated JAR file requires JavaFX runtime components that aren't included in the JAR itself. You must always specify the JavaFX module path when running the application:

Windows:
```
java --module-path "C:\path\to\javafx-sdk\lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar target\Assignmen-2-1.0-SNAPSHOT.jar
```

macOS:
```
java --module-path "/path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics -jar target/Assignmen-2-1.0-SNAPSHOT.jar
```

You cannot run the JAR file with a simple `java -jar` command due to the JavaFX module system requirements.

## Future Improvements
- Add more unit tests for all classes
- Implement password hashing for security
- Add more statistics and reporting features
- Extend the notification system to send emails instead of just logging to the console

## Contributors
- Group 9

## Contribution Score

Team members:
- Ye Thu Aung: 3 points
- Anh Bui Vie: 3 points
- Jinho Ju: 3 points

We agreed we equally contributed.

## License
This project is licensed under the MIT License - see the LICENSE file for details.
# assignment2
