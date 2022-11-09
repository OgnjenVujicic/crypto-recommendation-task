# -----------------------------------------------------------------------------
# STAGE 1: Setup
# -----------------------------------------------------------------------------
FROM openjdk:17-alpine
RUN mkdir /config
RUN mkdir /documents
COPY ./target/recommendation-test-task-*.jar app.jar

CMD ["java", "-jar", "app.jar", "-Pprod"]
