FROM openjdk:8-jre-alpine

ENV APP_TARGET target
ENV APP prometheus-demo.jar

RUN mkdir -p /opt
COPY ${APP_TARGET}/${APP} /opt

EXPOSE 8080

CMD exec java -Xms${JAVA_XMS:-512m} -Xmx${JAVA_XMX:-1024m} -jar /opt/${APP}