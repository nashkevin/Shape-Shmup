# EECS 393 Project
EECS 393 (Fall 2016) -- Software Engineering, Case Western Reserve University

## Contributors
* [Zachary Janice](https://github.com/zanice) ([znj](mailto:znj@case.edu?Subject=Cool%20EECS%20393%20project))
* [Joel Kalos](https://github.com/jidek) ([jdk138](mailto:jdk138@case.edu?Subject=Cool%20EECS%20393%20project))
* [Drew Mitchell](https://github.com/nerdydrew) ([atm62](mailto:atm62@case.edu?Subject=Cool%20EECS%20393%20project))
* [Kevin Nash](https://github.com/nashkevin) ([kjn33](mailto:kjn33@case.edu?Subject=Cool%20EECS%20393%20project))
* [Pete Thompson](https://github.com/bigpetenasty) ([pjt37](mailto:pjt37@case.edu?Subject=Cool%20EECS%20393%20project))
* [Kevin Wang](https://github.com/kevinwang95) ([kxw325](mailto:kxw325@case.edu?Subject=Cool%20EECS%20393%20project))

## Description
Our currently-untitled web browser game is a 2D multiplayer game in which you navigate as a simple geometric character and shoot down simple geometric enemies because, let's face it, none of us are artists.

### Features
* Use arrow keys or WASD to move
* Aim with the cursor and click to shoot
* Send chat messages

### Concept
When we finish the graphics, the game might look something like this. ![concept_art](http://i.imgur.com/CpOaOha.jpg)

## Instructions
First, be sure to have the latest versions of Java and Maven installed. Then, clone or download this repository. Finally, `make run` or `mvn jetty:run`. This will start the game server. In your browser, navigate to `0.0.0.0:8080` or, if running in Windows, `127.0.0.1:8080`. Clients who are connected to the same local network as you can also connect to your private IP address at port 8080 to access your server.

## Credits
Here is a comprehensive list of the pre-existing APIs, libraries, and other technologies that make our game possible.

* **Java SE 8**, Oracle Corporation, [license](http://www.oracle.com/technetwork/java/javase/terms/license/index.html)
 * object-oriented programming language
 * server-side game objects and environment are written in Java

* **Jetty 9**, The Eclipse Foundation, [license](https://www.eclipse.org/jetty/licenses.html)
 * WebSocket server API
 * clients communicate with the game server using  the Jetty WebSocketServlet

* **Maven 3**, The Apache Software Foundation, [license](asd.com)
 * build manager for Java projects
 * our source is built and tested automatically using Maven

* **PixiJS**, Goodboy Digital Ltd., [license](https://github.com/pixijs/pixi.js/blob/master/LICENSE)
 * 2D WebGL renderer
 * (*not fully implemented*) graphics are drawn on an HTML5 canvas in the player's browser using PixiJS