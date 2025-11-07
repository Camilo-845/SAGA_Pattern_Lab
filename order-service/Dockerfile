FROM maven:3.9-amazoncorretto-21-alpine AS build
WORKDIR /app
COPY pom.xml .
# build all dependencies for offline use
RUN mvn dependency:go-offline -B

# copy your other files
COPY ./src ./src

# build for release
RUN mvn package

FROM amazoncorretto:21-alpine
VOLUME /tmp
COPY --from=build /app/target/*.jar order_service.jar
ENTRYPOINT [ "java", "-jar", "/order_service.jar" ]
