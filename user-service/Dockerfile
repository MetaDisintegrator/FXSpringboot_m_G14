# 使用JDK17基础镜像
FROM eclipse-temurin:17-jre

# 将构建的JAR包复制到镜像中（动态路径）
COPY ./target/*.jar app.jar

# 复制配置文件到镜像中的绝对路径
COPY ./src/main/resources/application-local.yml /config/application.yml

# 暴露应用端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.import=optional:file:/config/"]