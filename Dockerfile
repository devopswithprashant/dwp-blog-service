# ==========================================
# STAGE 1: Build Custom Minimal JRE
# ==========================================
FROM alpine:3.20 AS jlink-builder

# Install OpenJDK 17 and binutils (provides 'objcopy' required for --strip-debug)
RUN apk add --no-cache openjdk17-jdk binutils

# Build lightweight JRE containing required Spring Boot modules (including java.desktop)
RUN /usr/lib/jvm/java-17-openjdk/bin/jlink \
    --add-modules java.base,java.desktop,java.instrument,java.management,java.naming,java.net.http,java.security.jgss,java.security.sasl,java.sql,java.transaction.xa,java.xml,jdk.unsupported,jdk.crypto.ec \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /custom-jre

# ==========================================
# STAGE 2: Ultra-Lean Production Runtime
# ==========================================
FROM alpine:3.20

LABEL maintainer="DevOps with Prashant"

# Install timezone data & C++ library needed by Java
RUN apk add --no-cache tzdata libstdc++

ENV TZ=UTC
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="$JAVA_HOME/bin:$PATH"

# Copy the custom JRE built in Stage 1
COPY --from=jlink-builder /custom-jre $JAVA_HOME

WORKDIR /app

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

# Copy Spring Boot JAR (Maven output: target/*.jar) with direct ownership
COPY --chown=spring:spring target/*.jar app.jar

USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]