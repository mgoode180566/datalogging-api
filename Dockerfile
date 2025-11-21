FROM amazoncorretto:17
WORKDIR /app
COPY target/recipe-plan-0.0.1-SNAPSHOT.jar recipe-plan-0.0.1-SNAPSHOT.jar
EXPOSE 8080
CMD ["java","-jar","recipe-plan-0.0.1-SNAPSHOT.jar"]