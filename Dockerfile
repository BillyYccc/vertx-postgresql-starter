FROM openjdk:8-jre-alpine

LABEL maintainer="Billy Yuan <billy112487983@gmail.com>" 

ENV VERTICLE_FILE vertx-postgresql-starter-1.0-SNAPSHOT-fat.jar
ENV HTTP_SERVER_CONFIG application-conf.json

# Set the location of the verticles
ENV VERTICLE_HOME /usr/verticles

EXPOSE 8080

# Copy your fat jar to the container
COPY build/libs/$VERTICLE_FILE $VERTICLE_HOME/
COPY src/main/configuration/$HTTP_SERVER_CONFIG $VERTICLE_HOME/

# Launch the verticle
WORKDIR $VERTICLE_HOME
ENTRYPOINT ["sh", "-c"]
CMD ["exec java -Dvertx.logger-delegate-factory-class-name=io.vertx.core.logging.SLF4JLogDelegateFactory \
 -jar $VERTICLE_FILE -conf $HTTP_SERVER_CONFIG"]