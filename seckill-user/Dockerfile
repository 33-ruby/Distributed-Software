# 基础镜像
FROM amazoncorretto:21

# 设置工作目录
WORKDIR /app

# 将打包好的 jar 文件复制到容器中
COPY target/seckill-user-1.0-SNAPSHOT.jar app.jar

# 暴露服务端口
EXPOSE 8081

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]


