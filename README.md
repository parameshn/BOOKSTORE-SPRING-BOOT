# Bookstore Management System

A comprehensive REST API-based bookstore management system built with Spring Boot, featuring JWT authentication and role-based access control.

## 🔗 Quick Links

- **GitHub Repository:** [https://github.com/parameshn/BOOKSTORE-SPRING-BOOT](https://github.com/parameshn/BOOKSTORE-SPRING-BOOT)
- **Postman Collection:** [Bookstore API Testing Collection](https://kxld-4969301.postman.co/workspace/Paramesh-workspace~638a0202-4881-45c9-8a40-544f0617cade/collection/44593529-aaed4180-a621-4149-b826-52b77b6b6f8b?action=share&creator=44593529)

## Features

- **User Authentication & Authorization**
  - JWT-based authentication
  - User registration and login
  - Role-based access control (User/Admin)
  
- **Author Management**
  - Full CRUD operations for authors
  - Advanced search and filtering
  - Author profile management with biography, nationality, and contact details
  
- **Security**
  - Bearer token authentication
  - Protected endpoints
  - Admin-only operations

## Technology Stack

- **Backend:** Spring Boot
- **Authentication:** JWT (JSON Web Tokens)
- **Database:** [Your Database - MySQL/PostgreSQL/H2]
- **Build Tool:** Maven/Gradle
- **Java Version:** [Your Java Version]

## Getting Started

### Prerequisites
- Java 8 or higher
- Maven/Gradle
- Database system (if not using H2)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/parameshn/BOOKSTORE-SPRING-BOOT.git
   cd BOOKSTORE-SPRING-BOOT
   ```

2. **Configure database connection**
   ```properties
   # application.properties
   spring.datasource.url=jdbc:mysql://localhost:3306/bookstore
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Build and run the application**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Access the application**
   ```
   Base URL: http://localhost:8383
   ```

## API Documentation

### Base URL
```
http://localhost:8383
```

### Authentication Endpoints

| Method | Endpoint | Description | Access Level |
|--------|----------|-------------|--------------|
| POST | `/api/auth/login` | User login | Public |
| POST | `/api/auth/register` | User registration | Public |

### Author Management Endpoints

| Method | Endpoint | Description | Access Level |
|--------|----------|-------------|--------------|
| GET | `/api/authors` | Get all authors | Authenticated |
| GET | `/api/authors/{id}` | Get author by ID | Authenticated |
| GET | `/api/authors/search` | Search authors by multiple criteria | Authenticated |
| GET | `/api/authors/search-by-nationality` | Filter authors by nationality | Authenticated |
| GET | `/api/authors/{id}/biography` | Get author biography | Authenticated |
| GET | `/api/authors/{id}/details` | Get detailed author information | Authenticated |
| POST | `/api/authors` | Create new author | Admin Only |
| PUT | `/api/authors/{id}` | Update existing author | Admin Only |
| DELETE | `/api/authors/{id}` | Delete author | Admin Only |

## API Testing

### Postman Collection
Import the complete Postman collection for comprehensive API testing:

**Collection Link:** [Bookstore API Postman Collection](https://kxld-4969301.postman.co/workspace/Paramesh-workspace~638a0202-4881-45c9-8a40-544f0617cade/collection/44593529-aaed4180-a621-4149-b826-52b77b6b6f8b?action=share&creator=44593529)

The collection includes:
- Pre-configured requests for all endpoints
- Sample request payloads and test data
- Environment variables configuration
- Authentication token management

### Environment Setup
Configure the following variables in your Postman environment:
```
base_url: http://localhost:8383
token: <your-jwt-token>
```

## API Usage Examples

### User Registration
```bash
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john.doe@example.com",
  "password": "SecurePassword123!"
}
```

### User Login
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "SecurePassword123!"
}
```

### Retrieve All Authors
```bash
GET /api/authors
Authorization: Bearer <your-jwt-token>
```

### Create Author (Admin Required)
```bash
POST /api/authors
Authorization: Bearer <admin-jwt-token>
Content-Type: application/json

{
  "name": "J.K. Rowling",
  "email": "jk.rowling@example.com",
  "biography": "British author best known for the Harry Potter series.",
  "birthDate": "1965-07-31",
  "nationality": "British"
}
```

### Search Authors
```bash
GET /api/authors/search?name=rowling&nationality=British
Authorization: Bearer <your-jwt-token>
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/bookstore/
│   │       ├── controller/     # REST Controllers
│   │       ├── service/        # Business Logic Layer
│   │       ├── repository/     # Data Access Layer
│   │       ├── model/          # Entity Classes
│   │       ├── config/         # Configuration Classes
│   │       └── security/       # Security Configuration
│   └── resources/
│       ├── application.properties
│       └── data.sql           # Initial data
```

## Authentication Workflow

1. **Registration/Login** - Obtain JWT token
2. **Token Authorization** - Include token in Authorization header: `Bearer <token>`
3. **Protected Endpoint Access** - Use valid token for secured endpoints
4. **Admin Operations** - Require admin role for modification operations

## API Load Overview

- **Total Endpoints:** 11
- **Authentication Module:** 2 endpoints (18%)
- **Author Management Module:** 9 endpoints (82%)
- **Public Access:** 2 endpoints
- **Protected Access:** 9 endpoints (5 user-accessible + 4 admin-only)

## Development Roadmap

### Planned Features
- Book management module
- Order processing system
- Inventory management
- Payment integration
- Advanced search with pagination
- Reporting and analytics
- Email notification system

### Technical Improvements
- Unit and integration testing
- API documentation with Swagger
- Caching implementation
- Performance optimization
- Logging and monitoring

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit your changes (`git commit -m 'Add new feature'`)
4. Push to the branch (`git push origin feature/new-feature`)
5. Create a Pull Request

## License

This project is licensed under the MIT License. See the LICENSE file for details.

## Support

For technical support or questions, please create an issue in the GitHub repository.

---

**Built with Spring Boot Framework**
