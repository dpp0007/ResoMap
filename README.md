# 🌐 ResoMap - Community Resource Hub

<div align="center">

![Java](https://img.shields.io/badge/Java-11-orange?style=for-the-badge&logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-17.0.2-blue?style=for-the-badge&logo=java)
![SQLite](https://img.shields.io/badge/SQLite-3.42-green?style=for-the-badge&logo=sqlite)
![Maven](https://img.shields.io/badge/Maven-3.8+-red?style=for-the-badge&logo=apache-maven)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

**A modern desktop application for managing community resources and connecting those in need with volunteers**

[Features](#-key-features) • [Installation](#-installation) • [Usage](#-usage) • [Documentation](#-documentation) • [Contributing](#-contributing)

</div>

---

## 📋 Table of Contents

- [About](#-about-the-project)
- [Key Features](#-key-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Installation](#-installation)
- [Usage](#-usage)
- [Project Structure](#-project-structure)
- [Database Schema](#-database-schema)
- [API Documentation](#-api-documentation)
- [Screenshots](#-screenshots)
- [Testing](#-testing)
- [Contributing](#-contributing)
- [Roadmap](#-roadmap)
- [License](#-license)
- [Acknowledgements](#-acknowledgements)

---

## 🎯 About The Project

**ResoMap** (Resource Management Platform) is a comprehensive desktop application designed to streamline community resource management. It connects individuals in need with available resources and volunteers, creating an efficient ecosystem for community support.

### Why ResoMap?

- **Centralized Management**: Single platform for all community resources
- **Real-time Tracking**: Monitor resource availability and request status
- **Role-based Access**: Separate interfaces for admins, volunteers, and requesters
- **Efficient Matching**: Automated assignment of volunteers to requests
- **Comprehensive Reporting**: Track resource utilization and community impact

### Who Is It For?

- **Community Organizations**: Manage donations and resource distribution
- **Non-profits**: Track aid requests and volunteer assignments
- **Local Governments**: Coordinate emergency resource allocation
- **Volunteer Groups**: Organize community support initiatives

---

## ✨ Key Features

### 🔐 Authentication & Security
- Secure user authentication with salted password hashing (SHA-256)
- Role-based access control (Admin, Volunteer, Requester)
- Account lockout protection (5 attempts, 15-minute lockout)
- Session management with automatic timeout

### 👥 User Management
- Three distinct user roles with tailored dashboards
- User registration with email verification
- Profile management and password reset
- Activity tracking and audit logs

### 📦 Resource Management
- Comprehensive resource catalog with categories
- Real-time inventory tracking
- Low-stock alerts and notifications
- Resource allocation and distribution tracking

### 📋 Request System
- Create and manage resource requests
- Urgency level classification (Low, Medium, High, Critical)
- Request status tracking (Pending, Assigned, In Progress, Completed, Cancelled)
- Automated volunteer assignment
- Request history and analytics

### 🎯 Dashboard Features

#### Admin Dashboard
- System-wide statistics and analytics
- User management (create, edit, delete users)
- Resource inventory management
- Request oversight and manual assignment
- System configuration and settings

#### Volunteer Dashboard
- View available requests
- Accept and manage assigned requests
- Update request status
- Communication with requesters
- Volunteer activity history

#### Requester Dashboard
- Create new resource requests
- Track request status
- View available resources
- Request history and feedback
- Communication with volunteers

### 📊 Reporting & Analytics
- Resource utilization reports
- Request fulfillment metrics
- Volunteer activity statistics
- Community impact dashboards

---

## 🛠 Tech Stack

### Core Technologies
- **Language**: Java 11
- **UI Framework**: JavaFX 17.0.2
- **Build Tool**: Apache Maven 3.8+
- **Database**: SQLite 3.42 (Development) / MySQL 8.0 (Production)

### Key Libraries & Frameworks
- **JavaFX Controls**: Modern UI components
- **JavaFX FXML**: Declarative UI design
- **JDBC**: Database connectivity
- **JUnit 5**: Unit testing
- **Mockito**: Mocking framework for tests

### Development Tools
- **IDE**: IntelliJ IDEA / Eclipse / VS Code
- **Version Control**: Git
- **Database Tools**: SQLite Browser / MySQL Workbench

---

## 🏗 Architecture

### High-Level System Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Presentation Layer                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │    Admin     │  │  Volunteer   │  │  Requester   │      │
│  │  Dashboard   │  │  Dashboard   │  │  Dashboard   │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│         │                  │                  │              │
│         └──────────────────┴──────────────────┘              │
│                            │                                 │
├────────────────────────────┼─────────────────────────────────┤
│                     Business Logic Layer                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ Authentication│  │   Resource   │  │   Request    │      │
│  │   Service    │  │   Service    │  │   Service    │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│         │                  │                  │              │
├────────────────────────────┼─────────────────────────────────┤
│                      Data Access Layer                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │   User DAO   │  │ Resource DAO │  │ Request DAO  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│         │                  │                  │              │
├────────────────────────────┼─────────────────────────────────┤
│                       Database Layer                         │
│  ┌───────────────────────────────────────────────────────┐  │
│  │              SQLite / MySQL Database                  │  │
│  │  ┌─────────┐  ┌──────────┐  ┌──────────┐            │  │
│  │  │  Users  │  │Resources │  │ Requests │            │  │
│  │  └─────────┘  └──────────┘  └──────────┘            │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Design Patterns Used

- **MVC (Model-View-Controller)**: Separation of concerns
- **DAO (Data Access Object)**: Database abstraction
- **Singleton**: Session management, database connection
- **Factory**: Service creation and dependency injection
- **Observer**: UI updates and event handling
- **Strategy**: Different authentication strategies

---

## 📥 Installation

### Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK) 11 or higher**
  ```bash
  java -version  # Should show version 11+
  ```

- **Apache Maven 3.8 or higher**
  ```bash
  mvn -version  # Should show version 3.8+
  ```

- **Git** (for cloning the repository)
  ```bash
  git --version
  ```

### Step-by-Step Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/yourusername/resomap.git
   cd resomap
   ```

2. **Build the Project**
   ```bash
   mvn clean install
   ```

3. **Run the Application**
   ```bash
   mvn javafx:run
   ```

### Alternative: Run with JAR

1. **Build JAR file**
   ```bash
   mvn clean package
   ```

2. **Run the JAR**
   ```bash
   java -jar target/community-resource-hub-1.0.0.jar
   ```

### Database Configuration

The application uses SQLite by default (no configuration needed). To use MySQL:

1. **Create MySQL Database**
   ```sql
   CREATE DATABASE community_hub;
   ```

2. **Update Configuration** in `DBConnection.java`:
   ```java
   private static final boolean USE_MYSQL = true;
   private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/community_hub";
   private static final String MYSQL_USER = "your_username";
   private static final String MYSQL_PASSWORD = "your_password";
   ```

---

## 🚀 Usage

### First Time Setup

1. **Launch the Application**
   ```bash
   mvn javafx:run
   ```

2. **Sample Data**: The application automatically creates sample data on first run:
   - 15 users (3 admins, 5 volunteers, 7 requesters)
   - 30 resources across 6 categories
   - 20 sample requests with various statuses

### Login Credentials

#### Admin Users
```
Username: admin
Password: Admin123!

Username: superadmin
Password: Super123!

Username: manager
Password: Manager123!
```

#### Volunteer Users
```
Username: volunteer1
Password: Volunteer123!

Username: volunteer2
Password: Volunteer123!
```

#### Requester Users
```
Username: user1
Password: User123!

Username: user2
Password: User123!
```

### Common Workflows

#### As a Requester

1. **Create a New Request**
   - Login with requester credentials
   - Click "Create New Request" button
   - Select resource category
   - Enter description and urgency level
   - Submit request

2. **Track Request Status**
   - View "My Requests" table
   - Check status updates
   - Communicate with assigned volunteer

#### As a Volunteer

1. **Accept Requests**
   - Login with volunteer credentials
   - Browse available requests
   - Click "Accept" on desired request
   - Update status as you fulfill the request

2. **Manage Assignments**
   - View assigned requests
   - Update progress
   - Mark as completed

#### As an Admin

1. **Manage Resources**
   - Add new resources
   - Update inventory levels
   - Set low-stock alerts

2. **Oversee Requests**
   - View all requests
   - Manually assign volunteers
   - Generate reports

---

## 📁 Project Structure

```
resomap/
├── src/
│   ├── main/
│   │   ├── java/com/communityhub/
│   │   │   ├── core/                    # Core utilities and base classes
│   │   │   │   ├── BaseController.java
│   │   │   │   ├── Constants.java
│   │   │   │   ├── ErrorHandler.java
│   │   │   │   └── ServiceFactory.java
│   │   │   ├── dao/                     # Data Access Objects
│   │   │   │   ├── BaseDAO.java
│   │   │   │   ├── UserDAO.java
│   │   │   │   ├── ResourceDAO.java
│   │   │   │   └── RequestDAO.java
│   │   │   ├── exception/               # Custom exceptions
│   │   │   │   ├── AuthenticationException.java
│   │   │   │   ├── DatabaseException.java
│   │   │   │   └── InvalidInputException.java
│   │   │   ├── model/                   # Domain models
│   │   │   │   ├── User.java
│   │   │   │   ├── Admin.java
│   │   │   │   ├── Volunteer.java
│   │   │   │   ├── Requester.java
│   │   │   │   ├── Resource.java
│   │   │   │   ├── Request.java
│   │   │   │   ├── UserRole.java
│   │   │   │   ├── RequestStatus.java
│   │   │   │   └── UrgencyLevel.java
│   │   │   ├── service/                 # Business logic
│   │   │   │   ├── AuthenticationService.java
│   │   │   │   ├── ResourceService.java
│   │   │   │   ├── RequestService.java
│   │   │   │   └── NotificationService.java
│   │   │   ├── ui/
│   │   │   │   ├── components/          # Reusable UI components
│   │   │   │   │   └── EmptyStateComponent.java
│   │   │   │   ├── controllers/         # FXML controllers
│   │   │   │   │   ├── LoginController.java
│   │   │   │   │   ├── AdminDashboardController.java
│   │   │   │   │   ├── VolunteerDashboardController.java
│   │   │   │   │   ├── RequesterDashboardController.java
│   │   │   │   │   └── NewRequestController.java
│   │   │   │   └── util/                # UI utilities
│   │   │   │       └── EnhancedTableCellFactory.java
│   │   │   ├── util/                    # General utilities
│   │   │   │   ├── DBConnection.java
│   │   │   │   ├── SessionManager.java
│   │   │   │   ├── PasswordUtils.java
│   │   │   │   ├── ValidationUtils.java
│   │   │   │   ├── DataInitializer.java
│   │   │   │   ├── LoginDiagnostic.java
│   │   │   │   └── LoggingConfig.java
│   │   │   └── CommunityHubApplication.java  # Main entry point
│   │   └── resources/
│   │       ├── assets/                  # Images and icons
│   │       │   └── icons/
│   │       ├── css/                     # Stylesheets
│   │       │   └── styles.css
│   │       ├── fxml/                    # UI layouts
│   │       │   ├── login.fxml
│   │       │   ├── register.fxml
│   │       │   ├── admin-dashboard.fxml
│   │       │   ├── volunteer-dashboard.fxml
│   │       │   ├── requester-dashboard.fxml
│   │       │   └── new-request.fxml
│   │       └── sql/                     # Database scripts
│   │           └── schema.sql
│   └── test/                            # Unit tests
│       └── java/com/communityhub/
├── logs/                                # Application logs
├── docs/                                # Documentation
│   ├── DATABASE.md
│   ├── LOGIN_CREDENTIALS.md
│   ├── LOGIN_TROUBLESHOOTING.md
│   ├── HOW_TO_REFRESH.md
│   ├── QUICK_REFERENCE_GUIDE.md
│   ├── QUICK_START_GUIDE.md
│   └── USER.md
├── community_hub.db                     # SQLite database
├── pom.xml                              # Maven configuration
├── .gitignore
└── README.md
```

---

## 🗄 Database Schema

The application uses a relational database with the following main tables:

- **users**: User accounts and authentication
- **resources**: Available community resources
- **requests**: Resource requests from community members
- **feedback**: User feedback and ratings

For detailed schema information, see [DATABASE.md](docs/DATABASE.md)

---

## 📡 API Documentation

### Service Layer APIs

#### AuthenticationService

```java
// Login
User login(String username, String password) throws AuthenticationException

// Register new user
User register(String username, String email, String password, 
              String confirmPassword, UserRole role) throws InvalidInputException

// Logout
void logout() throws AuthenticationException

// Get current user
User getCurrentUser()
```

#### ResourceService

```java
// Get all resources
List<Resource> getAllResources() throws DatabaseException

// Get resource by ID
Resource getResource(String resourceId) throws DatabaseException

// Create new resource
void createResource(Resource resource) throws DatabaseException

// Update resource
void updateResource(Resource resource) throws DatabaseException

// Delete resource
void deleteResource(String resourceId) throws DatabaseException
```

#### RequestService

```java
// Create new request
void createRequest(Request request) throws DatabaseException

// Get requests by user
List<Request> getRequestsByUser(String userId) throws DatabaseException

// Get all requests
List<Request> getAllRequests() throws DatabaseException

// Update request status
void updateRequestStatus(String requestId, RequestStatus status) 
    throws DatabaseException

// Assign volunteer to request
void assignVolunteer(String requestId, String volunteerId) 
    throws DatabaseException
```

---

## 📸 Screenshots

### Login Screen
![Login Screen](docs/screenshots/login.png)

### Admin Dashboard
![Admin Dashboard](docs/screenshots/admin-dashboard.png)

### Volunteer Dashboard
![Volunteer Dashboard](docs/screenshots/volunteer-dashboard.png)

### Requester Dashboard
![Requester Dashboard](docs/screenshots/requester-dashboard.png)

### Create New Request
![New Request Form](docs/screenshots/new-request.png)

---

## 🧪 Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserDAOTest

# Run with coverage
mvn clean test jacoco:report
```

### Test Structure

```
src/test/java/com/communityhub/
├── dao/
│   ├── UserDAOTest.java
│   ├── ResourceDAOTest.java
│   └── RequestDAOTest.java
├── service/
│   ├── AuthenticationServiceTest.java
│   ├── ResourceServiceTest.java
│   └── RequestServiceTest.java
└── util/
    ├── PasswordUtilsTest.java
    └── ValidationUtilsTest.java
```

---

## 🤝 Contributing

We welcome contributions from the community! Here's how you can help:

### How to Contribute

1. **Fork the Repository**
   ```bash
   git clone https://github.com/yourusername/resomap.git
   ```

2. **Create a Feature Branch**
   ```bash
   git checkout -b feature/AmazingFeature
   ```

3. **Make Your Changes**
   - Write clean, documented code
   - Follow existing code style
   - Add tests for new features

4. **Commit Your Changes**
   ```bash
   git commit -m 'Add some AmazingFeature'
   ```

5. **Push to Branch**
   ```bash
   git push origin feature/AmazingFeature
   ```

6. **Open a Pull Request**

### Coding Standards

- Follow Java naming conventions
- Use meaningful variable and method names
- Add JavaDoc comments for public methods
- Write unit tests for new features
- Keep methods focused and concise

### Reporting Issues

Found a bug? Have a feature request?

1. Check if the issue already exists
2. Create a new issue with:
   - Clear title and description
   - Steps to reproduce (for bugs)
   - Expected vs actual behavior
   - Screenshots if applicable

---

## 🗺 Roadmap

### Version 1.1 (Q1 2024)
- [ ] Email notifications for request updates
- [ ] SMS integration for urgent requests
- [ ] Mobile app (Android/iOS)
- [ ] Advanced search and filtering
- [ ] Export reports to PDF/Excel

### Version 1.2 (Q2 2024)
- [ ] Multi-language support
- [ ] Dark mode theme
- [ ] Real-time chat between users
- [ ] Resource reservation system
- [ ] Calendar integration

### Version 2.0 (Q3 2024)
- [ ] Web-based admin portal
- [ ] API for third-party integrations
- [ ] Machine learning for request matching
- [ ] Geographic mapping of resources
- [ ] Donation tracking and receipts

### Future Enhancements
- [ ] Blockchain for donation transparency
- [ ] AI-powered resource allocation
- [ ] Community forums
- [ ] Volunteer scheduling system
- [ ] Impact measurement dashboard

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2024 ResoMap Team

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 🙏 Acknowledgements

### Technologies & Libraries
- [JavaFX](https://openjfx.io/) - Modern UI framework for Java
- [SQLite](https://www.sqlite.org/) - Lightweight database engine
- [MySQL](https://www.mysql.com/) - Production database
- [Apache Maven](https://maven.apache.org/) - Build automation
- [JUnit 5](https://junit.org/junit5/) - Testing framework

### Design Resources
- [Flaticon](https://www.flaticon.com/) - Icons and graphics
- [Google Fonts](https://fonts.google.com/) - Typography
- [Coolors](https://coolors.co/) - Color palette generation

### Inspiration
- Community resource management best practices
- Open-source humanitarian projects
- Modern desktop application design patterns

### Contributors
Special thanks to all contributors who have helped make ResoMap better!

---

## 📞 Contact & Support

### Get Help
- 📧 Email: support@resomap.org
- 💬 Discord: [Join our community](https://discord.gg/resomap)
- 📖 Documentation: [docs.resomap.org](https://docs.resomap.org)
- 🐛 Issues: [GitHub Issues](https://github.com/yourusername/resomap/issues)

### Stay Updated
- ⭐ Star this repository
- 👀 Watch for updates
- 🔔 Follow us on [Twitter](https://twitter.com/resomap)

---

<div align="center">

**Made with ❤️ by the ResoMap Team**

[⬆ Back to Top](#-resomap---community-resource-hub)

</div>
