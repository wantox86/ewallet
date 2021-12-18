# simple-ewallet

This project is Simple Ewallet, which is made using Vertx as an HTTP server, and MySQL for the database.

First step, execute query DDL on `ddl/ddl.sql` to local mysql.

To build the "fat jar"

    ./gradlew shadowJar

To run the fat jar:

    java -jar build/libs/ewallet-1.0-SNAPSHOT-fat.jar 

(You can take that jar and run it anywhere there is a Java 8+ JDK. It contains all the dependencies it needs so you
don't need to install Vertx on the target machine).

Now point your browser at http://localhost:8888

profile postman : 

    https://www.getpostman.com/collections/a1faf447c3cd650cf0d0