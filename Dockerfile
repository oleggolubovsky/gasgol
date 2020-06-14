FROM reg.atgcom.com/images/node:nginx

RUN mkdir -p /var/www/frontend/
WORKDIR /var/www/frontend
COPY . /var/www/frontend/
VOLUME ["/var/log/nginx"]
COPY nginx.conf /etc/nginx/conf.d/default.conf
RUN rm /var/www/frontend/Dockerfile && rm /var/www/frontend/nginx.conf
EXPOSE 80 443
CMD ["nginx", "-g", "daemon off;"]

