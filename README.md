# vertx-postgresql-starter

This is a starter to build a [monolithic](http://microservices.io/patterns/monolithic.html) CRUD RESTful Web Service with [Vert.x](http://vertx.io/) stack and [postgreSQL](https://www.postgresql.org/).
It leverages [Gradle](https://gradle.org/) to build and [Docker](https://www.docker.com/) to boost deployment.

[![Vert.x Version](https://img.shields.io/badge/Vert.x-3.5.1-blue.svg)](https://github.com/eclipse/vert.x)
[![Build Status](https://travis-ci.org/BillyYccc/vertx-postgresql-starter.svg?branch=master)](https://travis-ci.org/BillyYccc/vertx-postgresql-starter)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/300d3703677b4cc3ace9e30ef6438586)](https://www.codacy.com/app/BillyYccc/vertx-postgresql-starter?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=BillyYccc/vertx-postgresql-starter&amp;utm_campaign=Badge_Grade)
[![license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/BillyYccc/vertx-postgresql-starter/blob/master/LICENSE)

## Prerequisites

* JDK 8+
* Docker
* Docker-compose

## Build

##### 1. Git clone this repo

`git clone https://github.com/BillyYccc/vertx-postgresql-starter.git $PROJECT_NAME`

##### 2. Go to the directory of the project

`cd $PROJECT_NAME`

##### 3. Generate a fat jar

`./gradlew shadowJar`

After a successful build, a fat jar file is generated in directory `$PROJECT_NAME/build/libs`

## Deployment

##### Just one-key deploy with docker-compose

`docker-compose up --build`

## Project

##### REST API

The project takes an easy example with an mini library, the REST API specification is [here](API_SPEC.md). 

##### Domain Logic
The Domain Logic is organized with [Transaction Script](https://martinfowler.com/eaaCatalog/transactionScript.html).

##### Database Degisn

![database](database.png)
