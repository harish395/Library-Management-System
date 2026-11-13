LIBRARY MANAGEMENT SYSTEM

Technology:
- HTML/CSS/JavaScript
- Java Servlet/JSP
- JDBC
- Oracle
- Maven
- Tomcat 9
- Java 21

SETUP:
1. Import the project as an Existing Maven Project in Eclipse.
2. In database.sql change nothing except ensure you are connected to your Oracle user.
3. Run database.sql in Oracle SQL Developer/SQL*Plus.
4. Open src/main/java/com/library/dao/DBConnection.java.
5. Replace YOUR_ORACLE_PASSWORD with your Oracle password.
6. Right-click project -> Maven -> Update Project.
7. Add Tomcat 9 server in Eclipse and add this project.
8. Start Tomcat.
9. Open:
   http://localhost:8080/LibraryManagementSystem/login.html

ADMIN:
username: admin
password: admin123

USER:
Register through registration.html.

FEATURES:
- Separate Admin/User roles
- Admin add/update/delete books
- Admin view users
- Admin view issued books
- User registration
- User login
- Session management
- Logout/session invalidation
- Search books
- Issue books
- Return books
- Fine calculation: Rs.5 per late day
- Activities/history
- Profile update

IMPORTANT:
This is a college/demo project. Passwords are stored as plain text for simplicity; a real application should use password hashing.
