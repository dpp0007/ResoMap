## 12. UI / UX Design Philosophy

### Design Goals

1. **Clarity**: Information presented clearly without clutter
2. **Efficiency**: Users accomplish tasks with minimal clicks
3. **Consistency**: Uniform design patterns across all pages
4. **Accessibility**: Usable by people with different abilities
5. **Responsiveness**: Works on desktop, tablet, and mobile devices

### Navigation Structure

**Primary Navigation**:
- Sidebar menu (persistent across all pages)
- Role-specific menu items
- Active page highlighting
- Logout button in footer

**Page Hierarchy**:
```
Login Page
    ↓
Dashboard (role-specific)
    ├── Resources Page
    ├── Requests Page
    ├── Feedback Page (if applicable)
    └── Admin Panel (admin only)
```

### Dashboard Design Rationale

**Admin Dashboard**:
- System-wide statistics (total resources, active requests, volunteers)
- Recent activity feed (all activities)
- Quick access to resource management
- Request oversight and assignment tools

**Volunteer Dashboard**:
- Active assignments count
- Completed requests count
- Performance metrics
- Recent activity (assigned requests only)

**Requester Dashboard**:
- Active requests count
- Completed requests count
- Cancelled requests count
- Recent activity (own requests only)

### Accessibility Considerations

**Color Contrast**:
- Text: #333 on #FFFFFF (21:1 ratio, AAA compliant)
- Links: #0066CC on #FFFFFF (8.6:1 ratio, AAA compliant)
- Buttons: #FFFFFF on #4CAF50 (4.5:1 ratio, AA compliant)

**Keyboard Navigation**:
- All interactive elements accessible via Tab key
- Focus indicators visible
- Form submission via Enter key
- Escape key closes modals

**Semantic HTML**:
```html
<header><!-- Page header --></header>
<nav><!-- Navigation menu --></nav>
<main><!-- Main content --></main>
<footer><!-- Footer --></footer>
<form><!-- Form elements --></form>
<button><!-- Clickable elements --></button>
<label><!-- Form labels --></label>
```

### Responsive Behavior

**Breakpoints**:
- Mobile: < 768px (single column layout)
- Tablet: 768px - 1024px (two column layout)
- Desktop: > 1024px (full layout)

**Responsive Elements**:
- Sidebar collapses to hamburger menu on mobile
- Tables stack vertically on small screens
- Forms adapt to screen width
- Navigation becomes vertical on mobile

**CSS Grid Implementation**:
```css
.grid {
    display: grid;
    gap: 20px;
}

@media (min-width: 768px) {
    .grid {
        grid-template-columns: repeat(2, 1fr);
    }
}

@media (min-width: 1024px) {
    .grid {
        grid-template-columns: repeat(3, 1fr);
    }
}
```

---

## 13. Build & Deployment

### Prerequisites

| Component | Requirement | Version |
|-----------|-------------|---------|
| Java Development Kit | Required | 11 or higher |
| Apache Maven | Required | 3.8 or higher |
| Apache Tomcat | Required | 9.0 or higher |
| Git | Optional | Latest |

**Verification**:
```bash
java -version          # Should show Java 11+
mvn -version          # Should show Maven 3.8+
```

### Build Steps

**Step 1: Clone Repository**
```bash
git clone https://github.com/yourusername/resomap.git
cd resomap
```

**Step 2: Clean Build**
```bash
mvn clean compile
```

**Step 3: Run Tests**
```bash
mvn test
```

**Step 4: Package WAR**
```bash
mvn package
```

**Output**: `target/community-resource-hub.war`

### WAR Generation

**Maven WAR Plugin Configuration**:
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-war-plugin</artifactId>
    <version>3.2.3</version>
    <configuration>
        <webXml>src/main/webapp/WEB-INF/web.xml</webXml>
    </configuration>
</plugin>
```

**WAR Contents**:
```
community-resource-hub.war
├── WEB-INF/
│   ├── web.xml (servlet configuration)
│   ├── classes/ (compiled Java classes)
│   └── lib/ (JAR dependencies)
├── jsp/ (JSP pages)
├── css/ (stylesheets)
├── js/ (JavaScript files)
└── META-INF/ (manifest)
```

### Tomcat Deployment

**Option 1: Manual Deployment**
```bash
# Copy WAR to Tomcat webapps directory
cp target/community-resource-hub.war $TOMCAT_HOME/webapps/

# Start Tomcat
$TOMCAT_HOME/bin/startup.sh

# Access application
# http://localhost:8080/community-resource-hub/
```

**Option 2: Tomcat Manager Deployment**
1. Access Tomcat Manager: `http://localhost:8080/manager`
2. Upload WAR file using web interface
3. Deploy and start application

**Option 3: Maven Plugin Deployment**
```bash
mvn tomcat7:deploy
```

### Configuration Notes

**Database Configuration**:
```java
// File: src/main/java/com/communityhub/util/DBConnection.java

// Development (SQLite)
private static final boolean USE_MYSQL = false;
private static final String DB_URL = "jdbc:sqlite:community_hub.db";

// Production (MySQL)
private static final boolean USE_MYSQL = true;
private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/community_hub";
private static final String MYSQL_USER = "your_username";
private static final String MYSQL_PASSWORD = "your_password";
```

**Session Configuration**:
```xml
<!-- web.xml -->
<session-config>
    <session-timeout>30</session-timeout>
</session-config>
```

**Logging Configuration**:
```java
// File: src/main/java/com/communityhub/util/LoggingConfig.java
Logger logger = Logger.getLogger("com.communityhub");
FileHandler fileHandler = new FileHandler("logs/application.log");
logger.addHandler(fileHandler);
```

---

## 14. Project Structure

### Directory Tree

```
resomap/
├── src/
│   ├── main/
│   │   ├── java/com/communityhub/
│   │   │   ├── servlet/
│   │   │   │   ├── LoginServlet.java
│   │   │   │   ├── DashboardServlet.java
│   │   │   │   ├── ResourceServlet.java
│   │   │   │   ├── RequestServlet.java
│   │   │   │   ├── FeedbackServlet.java
│   │   │   │   ├── SearchServlet.java
│   │   │   │   ├── filter/
│   │   │   │   │   └── AuthFilter.java
│   │   │   │   └── listener/
│   │   │   │       └── SessionListener.java
│   │   │   ├── service/
│   │   │   │   ├── AuthenticationService.java
│   │   │   │   ├── ResourceService.java
│   │   │   │   ├── RequestService.java
│   │   │   │   ├── ActivityService.java
│   │   │   │   └── FeedbackService.java
│   │   │   ├── dao/
│   │   │   │   ├── BaseDAO.java
│   │   │   │   ├── UserDAO.java
│   │   │   │   ├── ResourceDAO.java
│   │   │   │   ├── RequestDAO.java
│   │   │   │   ├── FeedbackDAO.java
│   │   │   │   └── ActivityDAO.java
│   │   │   ├── model/
│   │   │   │   ├── User.java
│   │   │   │   ├── Resource.java
│   │   │   │   ├── Request.java
│   │   │   │   ├── Feedback.java
│   │   │   │   ├── UserRole.java
│   │   │   │   ├── RequestStatus.java
│   │   │   │   └── UrgencyLevel.java
│   │   │   ├── dto/
│   │   │   │   └── ActivityDTO.java
│   │   │   ├── exception/
│   │   │   │   ├── CommunityHubException.java
│   │   │   │   ├── DatabaseException.java
│   │   │   │   ├── AuthenticationException.java
│   │   │   │   └── InvalidInputException.java
│   │   │   └── util/
│   │   │       ├── DBConnection.java
│   │   │       ├── ValidationUtils.java
│   │   │       ├── PasswordUtils.java
│   │   │       ├── RequestContext.java
│   │   │       └── LoggingConfig.java
│   │   ├── webapp/
│   │   │   ├── WEB-INF/
│   │   │   │   └── web.xml
│   │   │   ├── jsp/
│   │   │   │   ├── login.jsp
│   │   │   │   ├── dashboard.jsp
│   │   │   │   ├── resources.jsp
│   │   │   │   ├── requests.jsp
│   │   │   │   ├── new-request.jsp
│   │   │   │   ├── admin.jsp
│   │   │   │   ├── feedback.jsp
│   │   │   │   └── error.jsp
│   │   │   ├── css/
│   │   │   │   └── styles.css
│   │   │   └── js/
│   │   │       ├── search.js
│   │   │       ├── filters.js
│   │   │       └── validation.js
│   │   └── resources/
│   │       └── sql/
│   │           ├── schema_sqlite.sql
│   │           └── sample_data.sql
│   └── test/
│       └── java/com/communityhub/
│           ├── dao/
│           ├── service/
│           └── util/
├── pom.xml
├── README.md
├── LICENSE
└── .gitignore
```

### Package Purposes

| Package | Purpose |
|---------|---------|
| `servlet` | HTTP request handlers and filters |
| `service` | Business logic and rule enforcement |
| `dao` | Database abstraction layer |
| `model` | Domain objects and enums |
| `dto` | Data transfer objects |
| `exception` | Custom exception classes |
| `util` | Utility classes (DB, validation, logging) |

### Naming Conventions

**Classes**:
- Servlets: `*Servlet` (e.g., `LoginServlet`)
- Services: `*Service` (e.g., `AuthenticationService`)
- DAOs: `*DAO` (e.g., `UserDAO`)
- Exceptions: `*Exception` (e.g., `DatabaseException`)
- Utilities: `*Utils` (e.g., `ValidationUtils`)

**Methods**:
- Getters: `get*` (e.g., `getUsername()`)
- Setters: `set*` (e.g., `setUsername()`)
- Checkers: `is*` or `has*` (e.g., `isAdmin()`)
- Finders: `find*` or `get*` (e.g., `findByUsername()`)

**Variables**:
- Constants: `UPPER_CASE` (e.g., `MAX_LOGIN_ATTEMPTS`)
- Local variables: `camelCase` (e.g., `userName`)
- Instance variables: `camelCase` (e.g., `userId`)

---

## 15. Logging & Error Handling

### Logging Strategy

**Java Logger Configuration**:
```java
private static final Logger logger = Logger.getLogger(LoginServlet.class.getName());

// Log levels
logger.info("User logged in successfully: " + username);
logger.warning("Failed login attempt for user: " + username);
logger.severe("Database connection failed");
```

**Log Levels**:
| Level | Usage | Example |
|-------|-------|---------|
| INFO | Normal operations | "User logged in successfully" |
| WARNING | Recoverable issues | "Failed login attempt" |
| SEVERE | Critical errors | "Database connection failed" |

**Log Output**:
```
[2025-12-19 14:30:45] INFO: User logged in successfully: admin
[2025-12-19 14:31:12] WARNING: Failed login attempt for user: requester1
[2025-12-19 14:32:00] SEVERE: Database connection failed
```

### Error Pages

**404 Error Page** (`error.jsp`):
```jsp
<h1>Page Not Found</h1>
<p>The page you requested could not be found.</p>
<a href="${pageContext.request.contextPath}/dashboard">Return to Dashboard</a>
```

**500 Error Page** (`error.jsp`):
```jsp
<h1>Server Error</h1>
<p>An unexpected error occurred. Please try again later.</p>
<a href="${pageContext.request.contextPath}/dashboard">Return to Dashboard</a>
```

**web.xml Configuration**:
```xml
<error-page>
    <error-code>404</error-code>
    <location>/jsp/error.jsp</location>
</error-page>
<error-page>
    <error-code>500</error-code>
    <location>/jsp/error.jsp</location>
</error-page>
```

### Exception Hierarchy

```
Throwable
    ├── Exception
    │   ├── CommunityHubException (base)
    │   │   ├── DatabaseException
    │   │   ├── AuthenticationException
    │   │   └── InvalidInputException
    │   └── IOException, SQLException, etc.
    └── Error
```

**Exception Usage**:
```java
// Custom exception with user-friendly message
public class AuthenticationException extends CommunityHubException {
    private String userMessage;
    
    public AuthenticationException(String userMessage) {
        super(userMessage);
        this.userMessage = userMessage;
    }
    
    public String getUserMessage() {
        return userMessage;
    }
}

// Usage in servlet
try {
    User user = authService.login(username, password);
} catch (AuthenticationException e) {
    request.setAttribute("error", e.getUserMessage());
    request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
}
```

### Recovery Behavior

**Graceful Degradation**:
- Application continues running on non-critical errors
- User receives helpful error message
- System logs error for debugging
- No sensitive information exposed to user

**Example: Database Connection Failure**
```java
try {
    List<Resource> resources = resourceService.getAllResources();
    request.setAttribute("resources", resources);
} catch (DatabaseException e) {
    logger.log(Level.WARNING, "Failed to load resources", e);
    request.setAttribute("error", "Unable to load resources. Please try again later.");
    request.setAttribute("resources", new ArrayList<>());
}
request.getRequestDispatcher("/jsp/resources.jsp").forward(request, response);
```

