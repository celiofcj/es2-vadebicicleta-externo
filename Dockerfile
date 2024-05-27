FROM maven:3-openjdk-18-slim
EXPOSE 8080
WORKDIR /app
COPY ./ /app
ARG MAIL_USERNAME
ENV MAIL_USERNAME = ${MAIL_USERNAME}
ARG MAIL_PASSWORD
ENV MAIL_PASSWORD = ${MAIL_PASSWORD}
RUN mvn clean package -DskipTests
ENTRYPOINT java -jar /app/target/*.jar