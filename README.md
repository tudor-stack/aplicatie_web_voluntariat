# Volunteering Management Platform

A full-stack web application designed to connect volunteers with event organizers. This platform manages the complete lifecycle of volunteering activities, including event creation, volunteer enrollment, and participant feedback.

## Overview

The Volunteering Management Platform provides a comprehensive solution for organizing and participating in community service events. Organizers can manage events through a complete CRUD interface, while volunteers can browse opportunities, register for events, and provide feedback through an integrated review system.

## Features

### Core Functionality
- **Event Management System**: Full CRUD operations for event creation, modification, and deletion
- **Volunteer Registration**: Streamlined enrollment process for volunteering opportunities
- **Review and Rating System**: Feedback mechanism for volunteers to rate and review attended events
- **User Profile Management**: Personalized profiles tracking volunteer activity history
- **Relational Database Architecture**: Optimized data structure ensuring consistency and integrity

### Technical Highlights
- RESTful API design
- Responsive user interface
- Secure authentication and authorization
- Data persistence with PostgreSQL
- ORM-based database interactions

## Technology Stack

### Backend
- **Framework**: Spring Boot
- **Language**: Java
- **Dependencies**:
  - Spring Boot Web
  - Spring Data JPA
  - Hibernate (ORM)
- **Build Tool**: Maven

### Frontend
- **Languages**: JavaScript, HTML5, CSS3
- **Architecture**: Dynamic single-page application design

### Database
- **RDBMS**: PostgreSQL
- **ORM**: Hibernate

### Version Control
- **System**: Git

## Prerequisites

Before running this application, ensure you have the following installed:

- Java Development Kit (JDK) 11 or higher
- Maven 3.6 or higher
- PostgreSQL 12 or higher
- Git

## Installation and Setup

To run the project locally, follow the steps below:

### 1. Clone the repository

```bash
git clone https://github.com/tudor-stack/aplicatie_web_voluntariat.git
cd aplicatie_web_voluntariat
```

### 2. Configure the Database

Create a PostgreSQL database for the application:

```sql
CREATE DATABASE volunteering_platform;
```

### 3. Configure Application Properties

Update the database configuration in `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/volunteering_platform
spring.datasource.username=your_database_username
spring.datasource.password=your_database_password

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Server Configuration
server.port=8080
```

### 4. Install Dependencies

Build the project and download all required dependencies:

```bash
mvn clean install
```

### 5. Run the Application

Start the Spring Boot application:

```bash
mvn spring-boot:run
```

Alternatively, you can run the compiled JAR file:

```bash
java -jar target/aplicatie_web_voluntariat-0.0.1-SNAPSHOT.jar
```

### 6. Access the Application

Once the application starts successfully, access it through your web browser:

```
http://localhost:8080
```

## Project Structure

```
aplicatie_web_voluntariat/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/volunteering/
│   │   │       ├── controller/
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       └── service/
│   │   └── resources/
│   │       ├── static/
│   │       ├── templates/
│   │       └── application.properties
│   └── test/
├── pom.xml
└── README.md
```

## API Endpoints

The application exposes the following RESTful API endpoints:

### Events
- `GET /api/events` - Retrieve all events
- `GET /api/events/{id}` - Retrieve a specific event
- `POST /api/events` - Create a new event
- `PUT /api/events/{id}` - Update an existing event
- `DELETE /api/events/{id}` - Delete an event

### Volunteers
- `GET /api/volunteers` - Retrieve all volunteers
- `GET /api/volunteers/{id}` - Retrieve a specific volunteer profile
- `POST /api/volunteers` - Register a new volunteer
- `PUT /api/volunteers/{id}` - Update volunteer information

### Reviews
- `GET /api/reviews` - Retrieve all reviews
- `POST /api/reviews` - Submit a new review
- `GET /api/events/{id}/reviews` - Get reviews for a specific event

## Database Schema

The application uses a relational database with the following main entities:

- **Events**: Stores volunteering event information
- **Volunteers**: Maintains volunteer profiles and contact information
- **Enrollments**: Tracks volunteer registrations for events
- **Reviews**: Stores feedback and ratings for completed events

## Testing

Run the test suite with:

```bash
mvn test
```

## Build for Production

To create a production-ready build:

```bash
mvn clean package -DskipTests
```

The compiled JAR file will be available in the `target/` directory.

## Troubleshooting

### Common Issues

**Database Connection Failed**
- Verify PostgreSQL is running
- Check database credentials in `application.properties`
- Ensure the database exists

**Port Already in Use**
- Change the server port in `application.properties`
- Identify and stop the process using port 8080

**Maven Build Fails**
- Verify Java and Maven versions meet requirements
- Clear Maven cache: `mvn dependency:purge-local-repository`

## Contributing

Contributions are welcome. Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -m 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Open a Pull Request

## License

This project is available for educational and personal use.

## Contact

For questions or support, please open an issue in the GitHub repository.
