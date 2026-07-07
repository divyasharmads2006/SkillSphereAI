FROM eclipse-temurin:25-jdk

WORKDIR /app

COPY backend/ ./backend/
COPY frontend/ ./frontend/
COPY lib/ ./lib/

WORKDIR /app/backend

RUN javac -cp ".:/app/lib/*" *.java

EXPOSE 8080

CMD ["java", "-cp", ".:/app/lib/*", "BackendServer"]
