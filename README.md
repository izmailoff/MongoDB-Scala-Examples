Build Status
====================
[![Build Status](https://travis-ci.org/izmailoff/MongoDB-Scala-Examples.png)](https://travis-ci.org/izmailoff/MongoDB-Scala-Examples)

Abstract
====================

This is a project to try MongoDB Scala drivers and DSLs.
I will also try mocked or in-memory MongoDB for unit tests.

Project Structure
====================

It contains:
* SBT project files
* Scala source code
* Some Java code for comparison
* Scala unit tests source code
* No binaries or jar files except for SBT launcher

Dependencies
====================

Apps require mongod server process running on:

    localhost:27017

Tests can be run without MongoDB server because they use Fongo.
All Scala and Java dependencies (jars) will be downloaded by SBT.

MongoDB Installation Instructions
====================
Ubuntu
-------------
run in shell:

    sudo apt-get install mongodb
    vim /etc/mongodb.conf  # uncomment port
    service mongodb restart

Redhat/Fedora
-------------
run in shell:

    sudo yum install mongodb-server mongodb
    service mongod start

How To Run Applications
====================

SBT
-------------
Go to project directory and type 'sbt' or './sbt' (sbt executable is provided).
After this you can issue commands in SBT prompt:

    ; clean; compile; test; run

Alternatively you can type in shell:

    sbt update clean compile test run

The easiest way to run apps is to type 'run' in SBT and select app number from the list.

IDEs
-------------
### Eclipse
A project can be generated with:

    sbt eclipse
    
Use 'Import New Projects' in eclipse to open it.

### IntelliJ IDEA
You can generate a project by running this SBT command:

    sbt gen-idea
    
Some available options are:

    no-classifiers
    no-sbt-classifiers

