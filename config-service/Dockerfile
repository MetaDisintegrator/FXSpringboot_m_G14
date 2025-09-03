# 使用轻量级JDK17基础镜像
FROM openjdk:17-jdk-slim

# 将构建的JAR包复制到镜像中
COPY target/*.jar app.jar

# 复制配置文件到镜像中
COPY src/main/resources/config-prod/ /config/
COPY src/main/resources/application.yml /config/application.yml

# 暴露应用端口
EXPOSE 8088

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.import=optional:file:/config/"]