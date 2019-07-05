FROM java:8

MAINTAINER zhenye

EXPOSE 8980

VOLUME /tmp

RUN mkdir /app

RUN mkdir /app/logs

ADD target/spider-zhihu-1.0.0.jar /app/spider-zhihu-1.0.0.jar

ENTRYPOINT java -server -Xms512M -Xmx512M  -Xmn256M -XX:MetaspaceSize=128M -XX:MaxMetaspaceSize=128M -Xdebug -Xrunjdwp:server=y,transport=dt_socket,suspend=n,address=8080 -Xverify:none -XX:+DisableExplicitGC -Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom -Duser.timezone=GMT+08 -Dspring.profiles.active=prod -jar /app/spider-zhihu-1.0.0.jar
