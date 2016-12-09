run:
	mvn jetty:run

build:
	mvn package

runjar:
	java -jar target/dependency/jetty-runner.jar target/*.war

coverage:
	open target/site/jacoco-ut/index.html
