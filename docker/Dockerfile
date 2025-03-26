FROM docker.io/eclipse-temurin:20-alpine as builder

RUN apk update && apk add bash curl git maven

COPY . /build/

WORKDIR /build
RUN mvn package

####

FROM docker.io/eclipse-temurin:20-alpine

COPY --from=builder /build/target/*.jar /app/esp.jar
COPY docker/run.sh /app/

RUN chmod +x /app/run.sh && \
    adduser -S -D -H -h /var/empty esp && \
    chown esp /app

USER esp

WORKDIR /app

CMD ["/app/run.sh"]
 