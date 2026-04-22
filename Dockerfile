# syntax=docker/dockerfile:1.7
#
# Dockerfile multi-stage para a API Desafio Itaú.
# - Stage 1 (build): usa Maven + JDK 17 para compilar o JAR com cache de dependências.
# - Stage 2 (runtime): imagem mínima JRE 17 rodando como usuário não-root.

######## Stage 1: build ########
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /workspace

# Copia o wrapper primeiro para maximizar uso do cache de camadas
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

# Baixa dependências em uma camada separada
COPY pom.xml .
RUN ./mvnw --batch-mode --no-transfer-progress -q dependency:go-offline

# Compila e empacota
COPY src src
RUN ./mvnw --batch-mode --no-transfer-progress -q -DskipTests package \
 && mv target/*.jar target/app.jar

######## Stage 2: runtime ########
FROM eclipse-temurin:17-jre-jammy AS runtime

# Usuário não-root para boas práticas de segurança
RUN groupadd --system app && useradd --system --gid app --home-dir /app --shell /usr/sbin/nologin app
WORKDIR /app

COPY --from=build --chown=app:app /workspace/target/app.jar /app/app.jar

USER app
EXPOSE 8080

# Flags amigáveis a containers e observabilidade básica.
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"UP"' || exit 1

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
