# 该镜像需要依赖的基础镜像
FROM openjdk:8-jre-alpine

# 指定维护者的名字
MAINTAINER luojiarui luojiarui2@163.com

# 在docker中添加fontconfig 和 字体
# 该过程比较慢，可以参考https://lhalcyon.com/alpine-font-issue/
ENV LANG en_US.UTF-8
RUN apk add --update ttf-dejavu fontconfig  && rm -rf /var/cache/apk/*


# 将当前目录下的jar包复制到docker容器的/目录下
ADD ./target/document-sharing-site-1.0-SNAPSHOT.jar /app.jar

# 运行过程中创建一个app.jar文件
#RUN bash -c 'touch /app.jar'

# 声明服务运行在8082端口
EXPOSE 8082

# 指定docker容器启动时运行jar包
ENTRYPOINT ["java", "-jar","/app.jar"]
