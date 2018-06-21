# Bookmaker Company Wep Application
This is a training project of the course "Java Web Development" on the topic "Bookmaker Company".

## Starting the application
For starting the web application you should create a database connection and fill a database schema with data:
1. Change database access properties for your database server in src/main/resources/database.properties file.
2. Create a schema named "bookmaker" using info/create_scrypt.sql file.
3. Import the table with data in the schema named "bookmaker" using info/insert_scrypt.sql file.

## Used technologies
- Back-end: Java 8, Servlet, WebFilter, WebListener, JSP, JSTL, Custom Tags.
- Database: JDBC, MySQL, self-written connection-pool, transactions processing using ACID principles.
- Front-end: HTML5, CSS3, JavaScript, responsive and adaptive web design for devices with small screens without front-end frameworks.
- Tests: JUnit4.
- Other: Tomcat, Maven, Log4j2, Git, JavaDoc.

## Application architecture:
- **Client - Server** application architecture.
- **Layered architecture** where main backend layers are: Single FrontEnd Controller with Commands <-> Service Layer <-> DAO Layer <-> MySQL RDBMS.

## Used design patterns:
- MVC.
- Factory.
- Controller.
- Command.
- DAO.
- Wrapper.
- Lazy initialization.
- Singleton.
- Chain of responsibility.
- Other.

## Application features
### Common
- User role control.
- Custom user navigation prevention.
- Multi-language user interface (Russian and English languages).
- Limitation of data selection from database and pagination of long lists of request results.
- Frontend and backend validation of input parameters.

### Admin
- Login/logout.
- Add/edit/delete sport events.
- Add/edit events results.
- Delete invalid events.
- Pay winning bets.
- View players data.

### Analyst
- Login/logout.
- Add/edit/delete outcome coefficients for sport events.

### Player
- Registration, login/logout.
- Choose sport categories.
- View sport events and their outcomes with coefficients for chosen sport category.
- Choose outcome and make bet.
- View player history of bets and bets results.
- View player account information.
- View events results.
