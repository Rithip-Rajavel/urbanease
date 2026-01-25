# UrbanEase - Service Marketplace Backend

UrbanEase is a comprehensive service marketplace platform that connects customers with local service providers. This backend system provides all the necessary APIs for managing users, services, bookings, messaging, and more.

## Features

### 🔐 User Management & Authentication
- JWT-based secure authentication
- Role-based access control (Customer, Service Provider, Admin)
- Forgot password with OTP email verification
- Mobile number and username-based registration

### 🏠 Service Categories & Marketplace
- Multiple service categories (Home Maintenance, Domestic Help, Cleaning, etc.)
- Provider skill registration and expertise management
- Service browsing by category, location, and rating

### 📍 Location-Based Matching
- Real-time provider location tracking
- Haversine formula for distance calculation
- Nearby provider discovery within specified radius

### 📅 Service Request & Booking Flow
- Complete booking lifecycle (Pending → Accepted → In Progress → Completed)
- Provider availability management
- Booking status tracking and notifications

### 💬 In-App Messaging
- Real-time messaging between customers and providers
- Booking-specific chat conversations
- Read/unread message status

### ⭐ Ratings & Reviews
- 5-star rating system
- Review comments and feedback
- Automatic provider rating calculation

### 🔔 Notification System
- Real-time notifications for booking updates
- Email notifications for important events
- In-app notification management

### 👑 Admin & Moderation
- User management and moderation
- Service category management
- Provider verification system
- Dashboard statistics and analytics

## Technology Stack

- **Backend**: Spring Boot 3.5.10
- **Database**: PostgreSQL
- **Security**: Spring Security with JWT
- **Documentation**: Swagger/OpenAPI 3
- **Email**: Spring Mail
- **Validation**: Jakarta Bean Validation
- **Build Tool**: Maven
- **Java Version**: 17

## Project Structure

```
urbanease/
├── src/main/java/com/urbanease/
│   ├── config/                 # Configuration classes
│   ├── controller/             # REST API controllers
│   ├── dto/                    # Data Transfer Objects
│   ├── model/                  # JPA entities
│   ├── repository/             # Spring Data repositories
│   ├── security/               # Security configuration
│   └── service/                # Business logic services
├── src/main/resources/
│   ├── application.properties  # Main configuration
│   ├── application-dev.properties  # Development profile
│   ├── application-prod.properties  # Production profile
│   └── data.sql               # Sample data
└── pom.xml                    # Maven configuration
```

## Getting Started

### Prerequisites
- Java 17 or higher
- PostgreSQL 12 or higher
- Maven 3.6 or higher

### Database Setup

1. Create two databases:
   ```sql
   CREATE DATABASE urbanease_dev;
   CREATE DATABASE urbanease_prod;
   ```

2. Update database credentials in:
   - `src/main/resources/application-dev.properties`
   - `src/main/resources/application-prod.properties`

### Email Configuration

Update email settings in `application.properties`:
```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

### Running the Application

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd urbanease
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

## API Documentation

Once the application is running, you can access the Swagger UI at:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/api-docs`

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `POST /api/auth/forgot-password` - Send OTP for password reset
- `POST /api/auth/verify-otp-reset-password` - Verify OTP and reset password

### Services
- `GET /api/services/categories` - Get all service categories
- `GET /api/services/providers/nearby` - Find nearby providers
- `POST /api/services/providers/location` - Update provider location

### Bookings
- `POST /api/bookings` - Create new booking
- `POST /api/bookings/{id}/accept` - Accept booking (Provider)
- `POST /api/bookings/{id}/reject` - Reject booking (Provider)
- `POST /api/bookings/{id}/start` - Start service (Provider)
- `POST /api/bookings/{id}/complete` - Complete service (Provider)
- `POST /api/bookings/{id}/cancel` - Cancel booking (Customer)

### Messages
- `POST /api/messages` - Send message
- `GET /api/messages/conversation/{userId}` - Get conversation
- `GET /api/messages/booking/{bookingId}` - Get booking messages
- `GET /api/messages/unread` - Get unread messages

### Reviews
- `POST /api/reviews` - Create review
- `GET /api/reviews/provider/{providerId}` - Get provider reviews
- `GET /api/reviews/my-reviews` - Get my reviews

### Admin (Admin role required)
- `GET /api/admin/dashboard` - Get dashboard statistics
- `GET /api/admin/users` - Get all users
- `POST /api/admin/providers/{id}/verify` - Verify provider
- `GET /api/admin/bookings` - Get all bookings

## Environment Profiles

### Development Profile
- Active by default
- Database: `urbanease_dev`
- SQL logging enabled
- Debug logging enabled

### Production Profile
- Database: `urbanease_prod`
- SQL logging disabled
- Optimized logging
- DDL validation mode

## Security

### JWT Authentication
- Token expiration: 24 hours
- Bearer token format
- Role-based authorization

### Password Security
- BCrypt encryption
- Minimum 6 characters
- OTP-based password reset

### CORS Configuration
- Configured for cross-origin requests
- Supports all HTTP methods
- Allows credentials

## Sample Data

The application includes sample data in `data.sql`:
- 5 service categories
- 7 sample services
- 1 admin user (username: `admin`, password: `password`)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.

## Support

For support and questions:
- Email: support@urbanease.com
- Documentation: Check the Swagger UI
- Issues: Create an issue on GitHub
