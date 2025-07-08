@echo off
echo Building Simple Chat Application with Redis...
echo.

if not exist "target" mkdir target

echo Step 1: Prerequisites Check
echo - Java 17 or higher required
echo - Redis server required (localhost:6379)
echo - Maven 3.6 or higher required
echo.

echo Step 2: Start Redis Server
echo redis-server
echo.

echo Step 3: To build the project:
echo mvn clean install
echo.

echo Step 4: To run the application:
echo mvn spring-boot:run
echo.

echo Step 5: To run tests:
echo mvn test
echo.

echo Step 6: Access the application:
echo http://localhost:8080/
echo.

echo Note: Please ensure Redis is running and Maven is installed before running these commands.
pause
