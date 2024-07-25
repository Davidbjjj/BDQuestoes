# Use uma imagem base do JDK 17
FROM openjdk:17-jdk-slim

# Configura o diretório de trabalho
WORKDIR /app

# Copia o arquivo pom.xml e os arquivos de código fonte para o diretório de trabalho
COPY pom.xml .
COPY src ./src

# Baixa as dependências e compila o aplicativo
RUN ./mvnw package -DskipTests

# Expõe a porta que a aplicação Spring Boot usará
EXPOSE 8080

# Define o ponto de entrada para a execução da aplicação
ENTRYPOINT ["java", "-jar", "target/BancoQuestao-0.0.1-SNAPSHOT.jar"]
