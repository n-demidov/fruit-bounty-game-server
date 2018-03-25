# Fruit's Bounty

## Description

Fruits' Bounty - is the prototype of multiplayer game server.

The architecture is designed:
  * To quickly process players game actions (the priority business case).
  Their priority is higher than other requests (e.g., auth requests).
  To games do not "hang" when there are many authentication requests at the same time.
  * With using non-blocking multithreading.
  To enable vertical scale and increase of threads.

![alt text](https://github.com/n-demidov/fruit-bounty-game-server/blob/master/documents/1.%20architecture%20of%20non-blocking%20request%20processing.png?raw=true "Architecture of processing incoming requests: non-blocking multithreading & priority on game actions processing")

There is a weak reference between Game-Server and the Game due to the project is divided into three modules:
fruit-bounty-server, fruit-bounty-game-api and fruit-bounty-game.

All game logic is "protected" in the game module. And game-module implements game-api-module.
The server redirects incoming game actions to the game controller through the code API (Java code).
Answers from the game come either immediately or postponed - via a subscription (the Subscriber template).

The game module does not know anything about who will call it through the java API and subscribe to events.
To better understand the interaction between the server and the game, a separate module is allocated - the gaming API is a set of contracts (interfaces).
The server calls only them. The game-module provides an implementation.

![alt text](https://github.com/n-demidov/fruit-bounty-game-server/blob/master/documents/2.%20modules%20and%20some%20classes%20interactions.png?raw=true "Not full interaction between modules and some classes")

There are both unit and integration tests.

Also there is a caching strategy to quickly interact with a database.

Please, don't see on the client code :) It's small and just a rapid prototype with minimal functionality.

More details here (on russian): https://habrahabr.ru/post/351738/

## Technologies

Key Technologies: Java SE, Concurrency, Spring Framework.

Other technologies: Spring WebSocket, Spring Boot Test, JPA, PostgreSQL; client - HTML 5 Canvas.

## How to play online

Login in Facebook go to https://apps.facebook.com/fruit-bounty/

Or you can search the game in Facebook app catalog by key-words like "Fruit's Bounty".

## How to run locally

In `application.yml` set:
1. Database environment variables*: 
- JDBC_DATABASE_URL
- JDBC_DATABASE_USERNAME
- JDBC_DATABASE_PASS

2. Database environment variables* for Spring Boot tests: 
- TEST_JDBC_DATABASE_URL
- TEST_JDBC_DATABASE_USERNAME
- TEST_JDBC_DATABASE_PASS

3. Run Spring Boot application. And open the game on http://localhost:5000/app.html

You can change the port by set `PORT` environment variable or edit it in `application.yml` file.
By default the port is '5000'.

*Note: you can use docker to run local PosgreSQL instance:
1. Pull image [https://hub.docker.com/_/postgres/](https://hub.docker.com/_/postgres/)
2. Run docker container, e.g.: ```docker run --name h-postgres -e POSTGRES_PASSWORD=postgres -p:5432:5432 -d postgres:9.6.4```
3. Specify environment variable, e.g.:
   1. `JDBC_DATABASE_URL` = `jdbc:postgresql://192.168.99.100:5432/postgres`
   2. `JDBC_DATABASE_USERNAME` = `postgres`
   3. `JDBC_DATABASE_PASS` = `postgres`
  
  Note: To use docker on Windows 7 you can setup 'Docker Toolbox' soft. Which will start on specific IP which is shown on startup in a console.

## License
Fruit's Bounty is Open Source software released under the
[Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html)

The fruits images and the icon were taken from https://opengameart.org/content/flat-designed-fruits under "CC-BY 3.0" license (https://creativecommons.org/licenses/by/3.0/). Reference to the author of images is https://www.tumblr.com/blog/paulinefranky

The beach background image was taken from https://opengameart.org/content/beach-background under "CC0 1.0" license (https://creativecommons.org/publicdomain/zero/1.0/). Reference to the author of images is https://opengameart.org/users/tgfcoder

## Dates
It was written in ~Oct 2017 ~ Jan 2018.
