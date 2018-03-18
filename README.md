# Fruit's Bounty

## Description

Fruits' Bounty - is the prototype of multiplayer game server.

The architecture is designed to quickly process users game actions (the priority business case).
For this purposes also are using Java Concurrency and non-blocking multithreading.

Key Technologies: Java SE, Concurrency, Spring Framework.

Technologies were also used: Spring WebSocket, Spring Boot Test, JPA, PostgreSQL; client - HTML 5 Canvas.

## How to run locally

In `application.yml` set:
1. Database environment variables: 
- JDBC_DATABASE_URL
- JDBC_DATABASE_USERNAME
- JDBC_DATABASE_PASS

2. Database environment variables for Spring Boot tests: 
- TEST_JDBC_DATABASE_URL
- TEST_JDBC_DATABASE_USERNAME
- TEST_JDBC_DATABASE_PASS

For example, you can use use docker for locacl PosgreSQL:

1. Pull image [https://hub.docker.com/_/postgres/](https://hub.docker.com/_/postgres/)
2. Run docker container, e.g.: ```docker run --name h-postgres -e POSTGRES_PASSWORD=postgres -p:5432:5432 -d postgres:9.6.4```
3. Specify environment variable, e.g.:
  * JDBC_DATABASE_URL = jdbc:postgresql://192.168.99.100:5432/postgres
  * JDBC_DATABASE_USERNAME=postgres
  * JDBC_DATABASE_PASS=postgres
  
  Note: To use docker on Windows 7 you can setup 'Docker Toolbox' soft. Which will start on specific IP which is shown on startup in a console.

## License
Fruit's Bounty is Open Source software released under the
[Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html)
