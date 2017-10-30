# vertx-postgresql-starter

This is a starter to build a monolithic CRUD RESTful Web Service with [Vert.x](http://vertx.io/) stack and [postgreSQL](https://www.postgresql.org/).
It leverages [Gradle](gradle.org) to build and [Docker](https://www.docker.com/) to boost deployment.

[![Build Status](https://travis-ci.org/BillyYccc/vertx-postgresql-starter.svg?branch=master)](https://travis-ci.org/BillyYccc/vertx-postgresql-starter)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/300d3703677b4cc3ace9e30ef6438586)](https://www.codacy.com/app/BillyYccc/vertx-postgresql-starter?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=BillyYccc/vertx-postgresql-starter&amp;utm_campaign=Badge_Grade)
[![license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/BillyYccc/vertx-postgresql-starter/blob/master/LICENSE)

## Prerequisites

* JDK 8+
* Docker

## Build

##### 1. Git clone this repo

##### 2. Go to the directory of the project

##### 3. ./gradlew shadowJar

After a successful build, a fat jar file is generated in directory [build/libs]

## Deployment

##### 1. Build a Docker image
`docker build -t vertx-postgresql-starter .`

##### 2. Start a Docker container
`docker run -t -i -p 8080:8080 vertx-postgresql-starter`
