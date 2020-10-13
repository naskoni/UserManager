# User Manager
  
### Prerequisites
    - Java 14 JRE or JDK installed, tested also with Java 11
    - MySQL or MariaDB installed with created schema 'usermanager' or PostgreSQL, if not - the application can be 
     started with built-in H2 in-memory DB. You can use any DB you want, but the corresponding dependency must be
     added to 'build.gradle'.
    
    
### Deploy
    1. the settings in build.gradle -> deployToQA and deployToProd closures should be adjusted
    2. ./gradlew clean bootWar
    3. ./gradlew deployToQA (or deployToProd)
        
        
### Environment variables

By default the H2 in-memory DB will be used.
The application rely on environment variables in order to run with DB of your choice.
These need to be set up in your environment or in the Run Configuration of your IDE:

    - DB_USERNAME (default is 'sa')
    - DB_PASSWORD (default is 'sa')
    - DB_HOST (default 'jdbc:h2:mem:test'),         
    - USERMANAGER_DDL (default is 'create')
  

### Swagger UI

Swagger UI: when deployed on external server: {host}:{port}/usermanager/swagger-ui.html
when started with embedded Tomcat: {host}:{port}/swagger-ui.html

The API can be tested by the Swagger user interface or by Postman - such a collection
is provided with the project.

    
### ETags

    The API supports shallow ETags on GET requests. The value of response ETag header can be set to 
    request header 'If-None-Match' value. If the requested resource is not modified the response status
    will be: 304 Not Modified and no response body will be present.
