# 1. JDK가 들어 있는 기본 이미지 사용 (21 LTS)
FROM eclipse-temurin:21-jdk

# 2. 빌드된 JAR 파일을 컨테이너 안으로 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 3. 컨테이너가 노출할 포트 (Spring Boot 기본 8080)
EXPOSE 8081

# 4. 컨테이너 시작 시 실행할 명령
ENTRYPOINT ["java","-jar","/app.jar"]
