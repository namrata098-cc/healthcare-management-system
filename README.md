# Healthcare Management System

## Overview
The Healthcare Management System is a web-based application developed using Java and Spring Boot. It helps manage healthcare-related information, including patients, doctors, appointments, and medical records.

## Technologies Used
- Java 21
- Spring Boot
- Spring MVC
- Spring Security
- Thymeleaf
- Spring Data JPA / Hibernate
- MySQL
- JDBC
- Servlets
- Maven
- HTML and CSS

## Features
- User authentication and role-based access
- Patient and doctor management
- Appointment management
- Medical record management
- Feedback management
- Admin dashboard
- Database integration using JPA and JDBC
- Custom Servlet demonstration

## Project Structure
- `src/main/java` — Java source code, controllers, entities, repositories, and services
- `src/main/resources/templates` — Thymeleaf HTML templates
- `src/test/java` — application tests
- `pom.xml` — Maven dependencies and build configuration

## Prerequisites
- Java JDK 21
- Maven
- MySQL Server

## Running the Project
1. Clone or download this repository.
2. Create the required MySQL database.
3. Configure the database URL, username, and password in your local `application.properties` file.
4. Open a terminal in the project directory.
5. Run:

   ```bash
   mvn spring-boot:run
   ```

6. Open `http://localhost:8080` in your browser.

## JDBC and Servlet Demonstrations
- `/jdbc-report` — displays the total number of appointments using direct JDBC.
- `/servlet-demo` — demonstrates a custom Java Servlet endpoint.

## Testing
Run the automated tests using:

```bash
mvn test
```

## Author
Developed as a college project.