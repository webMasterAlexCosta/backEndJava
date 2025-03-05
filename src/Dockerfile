# Usando a imagem oficial do Maven com OpenJDK 21
FROM maven:3.8-openjdk-21

# Defina o diretório de trabalho no container
WORKDIR /app

# Copie o arquivo pom.xml e baixe as dependências (para cache de dependências)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copie todo o código do projeto para o container
COPY . .

# Execute o comando de construção para compilar o projeto
RUN mvn clean install

# Exponha a porta que o Spring Boot vai usar (geralmente a 8080)
EXPOSE 8080

# Comando para rodar a aplicação Spring Boot
CMD ["mvn", "spring-boot:run"]