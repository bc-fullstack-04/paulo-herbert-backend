#  Spotify Web Api Integration
## Description
This is an application for buying albums according to the data available on spotify, 
with a points system that is calculated according to the day of the week.   
Its have basic auth implemented, communication between services using rabbitmq, and a lot of tests to guarantee the application integrity.
## Install Guide
`git clone https://github.com/bc-fullstack-04/paulo-herbert-backend.git`

Install maven projects using:  
`mvn clean install`  
Before run the docker compose, make sure to package the maven projects using:  
`mvn clean package`    
in both `/app-integration-api` and `/app-user-api`  

Now, you need to build docker images, in the root repository folder type:  
`docker compose build `  
to get the containers running, type:  
`docker compose up -d`  
now everything's should be up and working
## Routes:
IntegrationApi:
```http request
    localhost:8082/api/{some resource path}
```
 UserAPI:
```http request
    localhost:8081/api/{some resource path}
```
 Docs:
```http request
    http://localhost:8081/api/swagger-ui/index.html
```
```http request
    http://localhost:8082/api/swagger-ui/index.html
```
## Technology used:
+ Spring
+ RabbitMQ
+ PostgreSQL
+ Junit 5
+ Mockito
+ Swagger
+ Docker
+ Maven  

Developed to SysMap Bootcamp 2024 - Backend Project - Paulo Herbert Ripardo Lucio  
I did my best :)
