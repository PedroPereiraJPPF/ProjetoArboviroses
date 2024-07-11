#build
FROM maven AS build

WORKDIR /app

COPY ./src ./src

COPY pom.xml .

RUN ln -sf /usr/share/zoneinfo/America/Sao_Paulo /etc/localtime && echo "America/Sao_Paulo" > /etc/timezone

RUN mvn -f ./pom.xml clean package

#run
FROM openjdk:17
	
ENV APP_NAME ProjetoArboviroses

COPY --from=build /app/target/${APP_NAME}.jar  /app/${APP_NAME}.jar

WORKDIR /app

CMD java -jar ${APP_NAME}.jar

EXPOSE 8080