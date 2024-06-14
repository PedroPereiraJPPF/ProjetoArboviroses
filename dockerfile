FROM openjdk:21
	
ENV APP_NAME ProjetoArboviroses

COPY ./target/${APP_NAME}.jar  /app/${APP_NAME}.jar

WORKDIR /app

CMD java -jar ${APP_NAME}.jar

EXPOSE 8080