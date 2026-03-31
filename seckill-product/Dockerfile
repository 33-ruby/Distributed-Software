# 第一阶段：在容器内编译，不占本机内存
FROM amazoncorretto:21-alpine AS builder
WORKDIR /build

# 安装 Maven
RUN apk add --no-cache maven

# 先复制 pom.xml，利用 Docker 缓存层加速后续构建
COPY pom.xml .
RUN mvn dependency:go-offline -q

# 再复制源码并编译
COPY src ./src
RUN mvn package -DskipTests -q

# 第二阶段：只保留运行需要的 jar
FROM amazoncorretto:21
WORKDIR /app
COPY --from=builder /build/target/seckill-product-1.0-SNAPSHOT.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]