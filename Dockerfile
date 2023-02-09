# 该镜像需要依赖的基础镜像
FROM openjdk:8-jre-alpine

# 指定维护者的名字
MAINTAINER luojiarui luojiarui2@163.com

# 创建新目录
RUN mkdir -p /home/coostack &&\
    mkdir -p /home/coostack/document/target

# 将当前目录下的jar包复制到docker容器的/目录下
COPY ./target/document-sharing-site-1.0-SNAPSHOT.jar /home/coostack/document/target/document-sharing-site-1.0-SNAPSHOT.jar
COPY src/config/application.yml /home/coostack/document/

# 声明服务运行在8082端口
EXPOSE 8082

# 指定docker容器启动时运行jar包
# ENTRYPOINT ["java", "-jar","/app.jar"]
