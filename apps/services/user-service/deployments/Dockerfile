# 只需要 JRE 运行环境，不需要 JDK 和 Maven，镜像更小
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 直接从外部复制构建好的 jar 包
# 注意：这里假设 jar 包会被放在构建上下文的 target 目录下
COPY target/*.jar app.jar

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]