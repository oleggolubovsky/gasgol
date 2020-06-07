FROM  reg.atgcom.com/images/java:j1.8

MAINTAINER Ilya I. "ilyuhan7@gmail.com"
WORKDIR /app

COPY ./*.jar ./

#COPY public public/

ADD supervisord.conf /etc/

EXPOSE 8080

ENTRYPOINT ["supervisord", "--nodaemon", "--configuration", "/etc/supervisord.conf"]
