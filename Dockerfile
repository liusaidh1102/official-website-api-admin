FROM openjdk:8-alpine

ENV TZ=Asia/Shanghai

COPY ./target/official-website-api-V1.0.jar /app.jar

# 全部完成后，开启，表明只使用 JIT编译器，增加运行速度，但是启动速度慢10倍
# ENTRYPOINT ["java","-Xcomp","-jar","/app.jar"]

ENTRYPOINT ["java","-jar","/app.jar"]