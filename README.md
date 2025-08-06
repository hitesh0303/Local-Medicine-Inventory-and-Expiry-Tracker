# Local-Medicine-Inventory-and-Expiry-Tracker

# Local Inventory Tracker

A full-stack application for managing medicine inventory with authentication.

## Features

- 🔐 Secure login system with HTTP Basic Authentication
- 👤 User registration and signup functionality
- 🔒 User-specific medicine inventory (each user sees only their own medicines)
- 💊 Medicine inventory management (CRUD operations)
- 🔍 Search functionality
- ⚠️ Expiry date tracking with visual warnings
- 📧 Email alerts for expiring medicines
- 🎨 Modern responsive UI

## Prerequisites

- Java 17 or higher
- Maven
- Node.js 16 or higher
- MySQL database

## Setup Instructions

### 1. Database Setup

1. Create a MySQL database named `medicine_db`
2. Update database credentials in `backend/src/main/resources/application.properties` if needed

### 2. Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Run the Spring Boot application:
   ```bash
   ./mvnw spring-boot:run
   ```

   The backend will start on `http://localhost:8080`

### 3. Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the React development server:
   ```bash
   npm start
   ```

   The frontend will start on `http://localhost:3000`

## Authentication

### Default Login Credentials
- **Username:** `hitesh`
- **Password:** `hitesh33`

### User Registration
Users can create new accounts through the signup form. The system supports:
- Username (3-20 characters)
- Password (minimum 6 characters)
- Email validation
- Full name
- Duplicate username/email prevention

## API Endpoints

- `POST /api/auth/signup` - User registration endpoint
- `POST /api/auth/login` - Login endpoint
- `GET /api/auth/status` - Check authentication status
- `POST /api/auth/logout` - Logout endpoint
- `GET /api/medicines` - Get all medicines
- `POST /api/medicines` - Add new medicine
- `PUT /api/medicines/{id}` - Update medicine
- `DELETE /api/medicines/{id}` - Delete medicine

## Authentication

The application uses HTTP Basic Authentication with database-backed user management. All API requests (except auth endpoints) require valid credentials. The frontend automatically includes authentication headers for all requests after successful login.

### User-Specific Data
- Each user has their own separate medicine inventory
- Users can only see, add, edit, and delete their own medicines
- Data is completely isolated between users
- Secure access control prevents cross-user data access

### Password Security
- Passwords are encrypted using BCrypt
- Minimum 6 characters required
- Secure password storage in database

## Troubleshooting

### 401 Unauthorized Errors
- Make sure you're logged in with valid credentials
- Check that the backend is running on port 8080
- Verify that the frontend is making requests to the correct backend URL

### Connection Errors
- Ensure both backend and frontend are running
- Check that the database is accessible
- Verify CORS configuration if accessing from a different domain

## Project Structure

```
Local Inventory Tracker/
├── backend/                 # Spring Boot backend
│   ├── src/main/java/
│   │   └── com/example/inventory/
│   │       ├── config/      # Security configuration
│   │       ├── controller/  # REST controllers
│   │       ├── model/       # Entity models
│   │       ├── repository/  # Data access layer
│   │       └── service/     # Business logic
│   └── src/main/resources/
│       └── application.properties
└── frontend/               # React frontend
    ├── src/
    │   ├── components/     # React components
    │   └── services/       # API service layer
    └── package.json
``` 



Manual Trigger Methods:
1. Trigger Immediate Expiry Check:

curl -X POST http://localhost:8080/api/test/trigger-expiry-check


# Check counts
curl -s http://localhost:8080/api/test/status

# Get all users
curl -s http://localhost:8080/api/test/users

# Pretty print JSON
curl -s http://localhost:8080/api/test/users | jq '.'

# Count users only
curl -s http://localhost:8080/api/test/status | jq '.users_count'
