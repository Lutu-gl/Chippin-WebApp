# Backend

## How to run it

### Start the backed
1. Execute `mvn clean package` 
2. Execute `mvn spring-boot:run`

### Start the backed with test data
Use the following command to start the backend with a populated test database. You can log in using the credentials:
- Email: rafael@chippin.com
- Password: password


Ensure the database is clean before running this command; otherwise, the test data will not be inserted:

`mvn spring-boot:run -Dspring-boot.run.profiles=generateData`
