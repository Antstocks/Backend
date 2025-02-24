# 1단계: 빌드 환경 설정 (필요한 경우)
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app
COPY ./build/libs/*.jar app.jar

# 2단계: 실행 환경 설정
EXPOSE 8081

# JAR 파일 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
