This is an unoficial version of Google's Guestbook sample made with Spring MVC. My intention here is to provide a starting point for someone who wants to develop a new project on App Engine using Spring MVC.

App Engine Java Guestbook with Spring MVC

Uses AppEngine SDK 1.8.9
Spring 4.0 (but works with Spring 3.X, just update the pom.xml)


## Sample guestbook for use with App Engine Java.

Requires [Apache Maven](http://maven.apache.org) 3.1 or greater, and JDK 7+ in order to run.

To build, run

    mvn package

Building will run the tests, but to explicitly run tests you can use the test target

    mvn test

To start the app, use the [App Engine Maven Plugin](http://code.google.com/p/appengine-maven-plugin/) that is already included in this demo.  Just run the command.

    mvn appengine:devserver

For further information, consult the [Java App Engine](https://developers.google.com/appengine/docs/java/overview) documentation.

To see all the available goals for the App Engine plugin, run

    mvn help:describe -Dplugin=appengine