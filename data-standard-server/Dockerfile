FROM java:8
MAINTAINER honglei_chen@dnt.com.cn
ARG workdir=app
WORKDIR /app
COPY target/data-standard-server-0.0.1-SNAPSHOT.jar /${workdir}/data-standard-server-1.0.jar
COPY commands.sh /${workdir}/commands.sh
RUN ["chmod", "+x", "commands.sh"]
ENTRYPOINT ["bash","commands.sh"]