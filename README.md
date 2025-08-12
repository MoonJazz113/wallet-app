Wallet App

Приложение для управления личными финансами.  
Позволяет добавлять транзакции, просматривать баланс и историю операций.  
Разработано на Java 17 с использованием Spring Boot, PostgreSQL и Docker Compose.

---

## 🛠 Стек технологий
- Java 17
- Spring Boot 3
- PostgreSQL
- Docker & Docker Compose
- Maven — сборка проекта

---

## Запуск проекта

### 1. Клонировать репозиторий
```bash
git clone https://github.com/username/wallet-app.git
cd wallet-app
```

---

2. Переменные окружения
Переменные окружения изменяются в .env в корне проекта:
APP_PORT=8082
SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/mydb
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=1234
POSTGRES_USER=postgres
POSTGRES_PASSWORD=1234
POSTGRES_DB=mydb

Приложение будет доступно по адресу:
http://localhost:8082

---

3. Запуск в Docker
docker-compose up --build
