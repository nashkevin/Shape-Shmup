# Shape Shmup
EECS 393 (Fall 2016) -- Software Engineering, Case Western Reserve University

## Contributors
* [Zachary Janice](https://github.com/zanice) ([znj](mailto:znj@case.edu?Subject=Cool%20EECS%20393%20project))
* [Joel Kalos](https://github.com/jidek) ([jdk138](mailto:jdk138@case.edu?Subject=Cool%20EECS%20393%20project))
* [Drew Mitchell](https://github.com/nerdydrew) ([atm62](mailto:atm62@case.edu?Subject=Cool%20EECS%20393%20project))
* [Kevin Nash](https://github.com/nashkevin) ([kjn33](mailto:kjn33@case.edu?Subject=Cool%20EECS%20393%20project))
* [Pete Thompson](https://github.com/bigpetenasty) ([pjt37](mailto:pjt37@case.edu?Subject=Cool%20EECS%20393%20project))
* [Kevin Wang](https://github.com/kevinwang95) ([kxw233](mailto:kxw233@case.edu?Subject=Cool%20EECS%20393%20project))

## Description
Shape Shmup game is a 2D multiplayer game in which you navigate as a simple geometric character and shoot down simple geometric enemies because, let's face it, none of us are artists.

### Features
* Use arrow keys or WASD to move
* Aim with the cursor and click to shoot
* Send chat messages
* Execute commands from the chatbox

### Graphics
Before we coded any of the graphics, we decided to style our game like this. ![concept_art](http://i.imgur.com/VYDHLe5.png)

This is how it currently looks.

![screenshot](http://i.imgur.com/QUft7cC.png)

## Instructions
First, be sure to have the latest versions of Java and Maven installed. Then, clone or download this repository. Finally, `make run` or `mvn jetty:run`. This will start the game server. In your browser, navigate to `127.0.0.1:8080`. Clients who are connected to the same local network as you can also connect to your local IP address at port 8080 to access your server.

You can also run the unit tests with the command `mvn test`.

To package the source code into a .war file, use `mvn package` (aliased to `make build`). You can run the .war file using the jetty-runner jar, which is automatically downloaded whenever you run `mvn package`. You can run jetty-runner with the command `java -jar target/dependency/jetty-runner.jar target/*.war` (aliased to `make runjar`).

## Credits
Here is a comprehensive list of the pre-existing APIs, libraries, and other technologies that make our game possible.

* **Java SE 8**, Oracle Corporation, [license](http://www.oracle.com/technetwork/java/javase/terms/license/index.html)
 * object-oriented programming language
 * server-side game objects and environment are written in Java

* **Jetty 9**, The Eclipse Foundation, [license](https://www.eclipse.org/jetty/licenses.html)
 * WebSocket server API
 * clients communicate with the game server using the Jetty WebSocketServlet

* **Maven 3**, The Apache Software Foundation, [license](http://maven.apache.org/ref/3.0/license.html)
 * build manager for Java projects
 * our source is built and tested automatically using Maven

* **PixiJS**, Goodboy Digital Ltd., [license](https://github.com/pixijs/pixi.js/blob/master/LICENSE)
 * 2D WebGL renderer
 * graphics are drawn on an HTML5 canvas in the player's browser using PixiJS

* **JS Intersections**, Kevin Lindsey, [license](https://github.com/thelonious/js-intersections/blob/master/LICENSE)
 * JavaScript library for calculating intersections of geometric objects
 * the barrier of the game arena is calculated using JS Intersections
