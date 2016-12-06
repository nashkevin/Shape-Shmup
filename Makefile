run:
	mvn jetty:run

build:
	mvn jetty:run-war

coverage:
	open target/site/jacoco-ut/index.html
