# Build stage — 多模块 Monorepo 上下文
FROM docker.io/library/maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /workspace
# 1. 复制父 POM（利用 Docker 缓存）
COPY apps/services/pom.xml apps/services/pom.xml
COPY apps/services/user-service/pom.xml apps/services/user-service/pom.xml
RUN mvn dependency:go-offline -f apps/services/user-service/pom.xml -B
# 2. 复制源码
COPY apps/services/user-service/src apps/services/user-service/src
# 3. 构建 user-service
RUN mvn package -DskipTests -f apps/services/user-service/pom.xml -B
# Runtime stage
FROM docker.io/library/eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /workspace/apps/services/user-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]