# 🏘️ Community Resource Hub

> A comprehensive JavaFX application for managing community resources, connecting volunteers with those in need, and facilitating efficient resource distribution.

[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://www.oracle.com/java/)
[![JavaFX](https://img.shields.io/badge/JavaFX-17.0.2-blue.svg)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-red.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![GitHub Repository](https://img.shields.io/badge/GitHub-ResoMap-blue.svg)](https://github.com/dpp0007/ResoMap)

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Getting Started](#-getting-started)
- [User Roles](#-user-roles)
- [Screenshots](#-screenshots)
- [Project Structure](#-project-structure)
- [Database Schema](#-database-schema)
- [Configuration](#-configuration)
- [Testing](#-testing)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🌟 Overview

The **Community Resource Hub** is a desktop application designed to streamline the process of connecting community members in need with available resources and volunteers. The system provides role-based access for administrators, volunteers, and requesters, ensuring efficient resource management and request fulfillment.

### Key Objectives

- 🤝 **Connect** volunteers with community members in need
- 📦 **Manage** community resources efficiently
- 📊 **Track** requests and resource distribution
- 🔒 **Secure** user data with role-based access control
- 📈 **Analyze** system performance with comprehensive reports

### 🎥 Try It Out

1. Clone and run the application (see [Quick Start](#-quick-start))
2. Use the test credentials below to explore different user roles
3. Experience the full workflow from request creation to completion

---

## ✨ Features

### 🔐 Authentication & Security

- **Secure Login System** with password hashing (BCrypt)
- **User Registration** with email validation
- **Role-Based Access Control** (Admin, Volunteer, Requester)
- **Session Management** with automatic timeout
- **Password Requirements** enforcement
- **Audit Logging** for security compliance

### 👨‍💼 Admin Dashboard

#### Resource Management
- ✅ Add, edit, and delete resources
- ✅ Track resource availability and quantities
- ✅ Categorize resources (Food, Clothing, Medical, Shelter, etc.)
- ✅ Monitor resource distribution statistics

#### User Management
- ✅ View all registered users
- ✅ Filter by role (Admin, Volunteer, Requester)
- ✅ Search users by username or email
- ✅ Track user statistics (Total, Active, by Role)
- ✅ Manage user permissions

#### Request Overview
- ✅ Monitor all help requests in real-time
- ✅ Filter by status (Pending, Assigned, Completed)
- ✅ Filter by urgency level
- ✅ View request statistics and completion rates
- ✅ Assign volunteers to requests

#### System Reports
- ✅ Generate comprehensive system reports
- ✅ Customizable date range selection
- ✅ Key metrics dashboard:
  - Total requests and completion rate
  - Average response time
  - Active volunteers count
  - Resources distributed
  - Average user ratings
- ✅ Detailed reports by category:
  - Request statistics by type
  - User activity tracking
  - Resource usage analysis
- ✅ Export reports in multiple formats

#### System Settings
- ✅ **General Settings**: Application name, organization info, contact details
- ✅ **Request Settings**: Max requests per user, timeout configuration, urgency thresholds
- ✅ **Database Settings**: Connection configuration, backup management, optimization tools
- ✅ **Security Settings**: Password policies, session timeout, two-factor authentication
- ✅ **Database Maintenance**:
  - One-click database backup
  - Database optimization
  - Cache clearing
- ✅ **Audit Log Viewer**: View system activities with detailed table
- ✅ **GDPR Compliance**: Export user data in multiple formats (HTML/PDF, JSON, CSV, XML)

### 🙋 Volunteer Dashboard

#### Available Requests
- ✅ Browse all pending help requests
- ✅ Filter by urgency level
- ✅ View detailed request information
- ✅ Accept requests with one click
- ✅ Real-time request updates

#### My Assignments
- ✅ View all assigned requests
- ✅ Filter by status
- ✅ Update request status (In Progress, Completed)
- ✅ Track completion history
- ✅ View requester contact information

#### Resources Browser
- ✅ Modern card-based resource display
- ✅ Search by name, category, or location
- ✅ Filter by category
- ✅ View resource availability in real-time
- ✅ Beautiful themed cards (Food, Clothing, Medical, Shelter)
- ✅ Contact information for each resource

#### Profile Section
- ✅ View volunteer statistics:
  - Active assignments count
  - Completed requests count
  - Impact score calculation
- ✅ Member since information
- ✅ Update profile information
- ✅ View activity history

### 📝 Requester Dashboard

#### Dashboard Overview
- ✅ Quick statistics (Total requests, Pending, Completed)
- ✅ Recent activity feed
- ✅ Quick action buttons

#### Browse Resources
- ✅ View all available resources
- ✅ Search and filter capabilities
- ✅ Detailed resource information
- ✅ Check availability status

#### My Requests
- ✅ View all submitted requests
- ✅ Track request status in real-time
- ✅ View assigned volunteer information
- ✅ Request history and timeline

#### New Request
- ✅ Submit new help requests
- ✅ Select from available resources
- ✅ Set urgency level (Low, Medium, High, Critical)
- ✅ Add detailed descriptions
- ✅ Specify quantity needed

#### Feedback System
- ✅ Submit feedback on completed requests
- ✅ Rate services (1-5 stars)
- ✅ Provide detailed comments
- ✅ Anonymous feedback option

#### Profile Management
- ✅ Update personal information
- ✅ Change password
- ✅ View account statistics

---

## 🛠️ Technology Stack

### Core Technologies

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 11+ | Core programming language |
| **JavaFX** | 17.0.2 | UI framework |
| **Maven** | 3.8+ | Build automation and dependency management |
| **SQLite** | 3.42.0 | Embedded database |
| **BCrypt** | 0.10.2 | Password hashing |

### Key Libraries

```xml
<!-- JavaFX Components -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>17.0.2</version>
</dependency>

<!-- Database -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.42.0.0</version>
</dependency>

<!-- Security -->
<dependency>
    <groupId>at.favre.lib</groupId>
    <artifactId>bcrypt</artifactId>
    <version>0.10.2</version>
</dependency>
```

---

## 🚀 Getting Started

### 🔥 Quick Start

```bash
# Clone the repository
git clone https://github.com/dpp0007/ResoMap.git
cd ResoMap

# Build and run
mvn clean javafx:run
```

> **Note**: The application will automatically create the database and sample data on first run.

### Prerequisites

- **Java Development Kit (JDK)** 11 or higher
- **Apache Maven** 3.8 or higher
- **Git** (for cloning the repository)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/dpp0007/ResoMap.git
   cd ResoMap
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn javafx:run
   ```

### First Time Setup

The application will automatically:
- Create the SQLite database (`community_hub.db`)
- Initialize database schema
- Create sample data for testing

---

## 👥 User Roles

### 🔑 Test Credentials

| Role | Username | Password | Description |
|------|----------|----------|-------------|
| **Admin** | `admin` | `pass` | Full system access and management |
| **Volunteer** | `volunteer1` | `pass` | Accept and fulfill requests |
| **Volunteer** | `volunteer2` | `pass` | Accept and fulfill requests |
| **Requester** | `requester1` | `pass` | Submit help requests |
| **Requester** | `requester2` | `pass` | Submit help requests |

### Role Permissions

#### 👨‍💼 Administrator
- ✅ Full access to all features
- ✅ Manage users and roles
- ✅ Manage resources
- ✅ View all requests
- ✅ Generate system reports
- ✅ Configure system settings
- ✅ Access audit logs
- ✅ Export user data (GDPR)

#### 🙋 Volunteer
- ✅ View available requests
- ✅ Accept and fulfill requests
- ✅ Update request status
- ✅ Browse resources
- ✅ Manage profile
- ✅ View assignment history

#### 📝 Requester
- ✅ Submit help requests
- ✅ Browse available resources
- ✅ Track request status
- ✅ Provide feedback
- ✅ Manage profile
- ✅ View request history

---

## 📸 Screenshots

### Login Screen
Clean and modern authentication interface with role-based access.

### Admin Dashboard
Comprehensive overview with statistics, resource management, and system controls.

### Volunteer Dashboard
Intuitive interface for browsing requests and managing assignments with modern card-based design.

### Requester Dashboard
User-friendly interface for submitting requests and tracking status.

---

## 📁 Project Structure

```
ResoMap/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/communityhub/
│   │   │       ├── CommunityHubApplication.java
│   │   │       ├── dao/                    # Data Access Objects
│   │   │       │   ├── BaseDAO.java
│   │   │       │   ├── UserDAO.java
│   │   │       │   ├── ResourceDAO.java
│   │   │       │   ├── RequestDAO.java
│   │   │       │   └── FeedbackDAO.java
│   │   │       ├── exception/              # Custom Exceptions
│   │   │       │   ├── AuthenticationException.java
│   │   │       │   ├── DatabaseException.java
│   │   │       │   └── InvalidInputException.java
│   │   │       ├── model/                  # Domain Models
│   │   │       │   ├── User.java
│   │   │       │   ├── Resource.java
│   │   │       │   ├── Request.java
│   │   │       │   ├── Feedback.java
│   │   │       │   ├── RequestStatus.java
│   │   │       │   ├── UrgencyLevel.java
│   │   │       │   └── UserRole.java
│   │   │       ├── service/                # Business Logic
│   │   │       │   ├── AuthenticationService.java
│   │   │       │   ├── ResourceService.java
│   │   │       │   ├── RequestService.java
│   │   │       │   └── FeedbackService.java
│   │   │       ├── ui/
│   │   │       │   └── controllers/        # JavaFX Controllers
│   │   │       │       ├── LoginController.java
│   │   │       │       ├── RegisterController.java
│   │   │       │       ├── AdminDashboardController.java
│   │   │       │       ├── VolunteerDashboardController.java
│   │   │       │       ├── RequesterDashboardController.java
│   │   │       │       └── SystemSettingsController.java
│   │   │       └── util/                   # Utilities
│   │   │           ├── DBConnection.java
│   │   │           ├── PasswordUtil.java
│   │   │           ├── ValidationUtils.java
│   │   │           ├── SessionManager.java
│   │   │           └── LoggingConfig.java
│   │   └── resources/
│   │       ├── css/
│   │       │   └── styles.css              # Application Styling
│   │       ├── fxml/                       # UI Layouts
│   │       │   ├── login.fxml
│   │       │   ├── register.fxml
│   │       │   ├── admin-dashboard.fxml
│   │       │   ├── volunteer-dashboard.fxml
│   │       │   ├── requester-dashboard.fxml
│   │       │   ├── resource-management.fxml
│   │       │   ├── user-management.fxml
│   │       │   ├── request-overview.fxml
│   │       │   ├── system-reports.fxml
│   │       │   └── system-settings.fxml
│   │       └── sql/
│   │           └── init_database.sql       # Database Schema
├── logs/                                   # Application Logs
├── exports/                                # User Data Exports
├── community_hub.db                        # SQLite Database
├── pom.xml                                 # Maven Configuration
└── README.md                               # This File
```

---

## 🗄️ Database Schema

### Tables

#### **users**
Stores user account information with role-based access.

| Column | Type | Description |
|--------|------|-------------|
| user_id | VARCHAR(36) | Primary key (UUID) |
| username | VARCHAR(50) | Unique username |
| email | VARCHAR(100) | User email address |
| password_hash | VARCHAR(255) | BCrypt hashed password |
| role | ENUM | ADMIN, VOLUNTEER, REQUESTER |
| display_name | VARCHAR(100) | User's display name |
| created_at | TIMESTAMP | Account creation date |
| is_active | BOOLEAN | Account status |

#### **resources**
Manages available community resources.

| Column | Type | Description |
|--------|------|-------------|
| resource_id | VARCHAR(36) | Primary key (UUID) |
| name | VARCHAR(100) | Resource name |
| description | TEXT | Detailed description |
| category | VARCHAR(50) | Resource category |
| quantity | INT | Available quantity |
| location | VARCHAR(200) | Resource location |
| contact_info | VARCHAR(200) | Contact information |
| is_active | BOOLEAN | Availability status |

#### **requests**
Tracks help requests from community members.

| Column | Type | Description |
|--------|------|-------------|
| request_id | VARCHAR(36) | Primary key (UUID) |
| user_id | VARCHAR(36) | Foreign key to users |
| resource_id | VARCHAR(36) | Foreign key to resources |
| volunteer_id | VARCHAR(36) | Assigned volunteer |
| description | TEXT | Request details |
| urgency | VARCHAR(50) | Urgency level |
| status | VARCHAR(50) | Request status |
| quantity_requested | INT | Quantity needed |
| created_at | TIMESTAMP | Request creation date |
| assigned_at | TIMESTAMP | Assignment date |
| completed_at | TIMESTAMP | Completion date |

#### **feedback**
Stores user feedback and ratings.

| Column | Type | Description |
|--------|------|-------------|
| feedback_id | VARCHAR(36) | Primary key (UUID) |
| user_id | VARCHAR(36) | Foreign key to users |
| request_id | VARCHAR(36) | Related request |
| feedback_type | ENUM | GENERAL, REQUEST_SPECIFIC, SYSTEM_IMPROVEMENT |
| rating | INT | Rating (1-5) |
| comments | TEXT | Feedback comments |
| is_anonymous | BOOLEAN | Anonymous flag |
| created_at | TIMESTAMP | Submission date |

---

## ⚙️ Configuration

### Application Settings

Settings can be configured through the Admin Dashboard → Settings page:

- **General**: Application name, organization, contact information
- **Requests**: Max requests per user, timeout settings, urgency alerts
- **Database**: Connection pool size, backup frequency
- **Security**: Password requirements, session timeout, audit logging

### Database Configuration

The application uses SQLite by default. Connection settings are in:
```java
src/main/java/com/communityhub/util/DBConnection.java
```

### Logging Configuration

Logs are stored in the `logs/` directory. Configure logging in:
```java
src/main/java/com/communityhub/util/LoggingConfig.java
```

---

## 🧪 Testing

### Running Tests

```bash
mvn test
```

### Test Coverage

The project includes:
- Unit tests for service layer
- Integration tests for database operations
- UI component tests

### Manual Testing

Use the provided test credentials to test different user roles and workflows.

---

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/AmazingFeature
   ```
3. **Commit your changes**
   ```bash
   git commit -m 'Add some AmazingFeature'
   ```
4. **Push to the branch**
   ```bash
   git push origin feature/AmazingFeature
   ```
5. **Open a Pull Request**

### Coding Standards

- Follow Java naming conventions
- Write meaningful commit messages
- Add JavaDoc comments for public methods
- Include unit tests for new features
- Update README.md for significant changes

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 📞 Contact & Support

- **Project Repository**: [ResoMap on GitHub](https://github.com/dpp0007/ResoMap)
- **Project Owner**: [@dpp0007](https://github.com/dpp0007)
- **Issue Tracker**: [GitHub Issues](https://github.com/dpp0007/ResoMap/issues)
- **Discussions**: [GitHub Discussions](https://github.com/dpp0007/ResoMap/discussions)

---

## 🙏 Acknowledgments

- JavaFX community for excellent documentation
- SQLite for the reliable embedded database
- BCrypt library for secure password hashing
- All contributors and testers

---

## 📊 Project Status

**Current Version**: 1.0.0  
**Status**: ✅ Production Ready  
**Last Updated**: November 2025  
**Repository**: [dpp0007/ResoMap](https://github.com/dpp0007/ResoMap)

### ✨ Recent Updates

- ✅ **Clean Architecture**: Well-organized codebase with proper separation of concerns
- ✅ **Code Quality**: Comprehensive linting cleanup and optimization
- ✅ **Icon Consistency**: Fixed icon sizing issues for uniform UI display  
- ✅ **Professional Documentation**: Complete README with setup instructions
- ✅ **GitHub Ready**: Proper .gitignore and repository structure
- ✅ **FXML Architecture**: Modern JavaFX UI with proper controller bindings
- ✅ **Database Integration**: SQLite with comprehensive DAO pattern implementation

---

<div align="center">

**Made with ❤️ for the Community by [@dpp0007](https://github.com/dpp0007)**

⭐ **Star this repository if you found it helpful!**

[🔗 **View on GitHub**](https://github.com/dpp0007/ResoMap) | [⬆ Back to Top](#-community-resource-hub)

</div>
