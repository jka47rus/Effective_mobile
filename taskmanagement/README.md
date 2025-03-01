# Task management system

Система управления задачами представляет собой RESTful API приложение, разработанное с использованием Java, Spring Boot,
Spring Security. Система позволяет пользователям создавать, редактировать, удалять и просматривать задачи. Каждая задача
содержит заголовок, описание, статус, приоритет, комментарии, а также информацию об авторе и исполнителе.

Основные функциональные возможности:  
Аутентификация и Авторизация:   
Пользователи могут регистрироваться и входить в систему с использованием email и пароля.  
Аутентификация осуществляется через JWT.

Ролевая система:  
Администратор может управлять всеми задачами: создавать, редактировать, удалять, назначать исполнителей, менять статусы
и приоритеты, оставлять комментарии.  
Пользователь может управлять только теми задачами, где он указан как исполнитель: менять статусы, оставлять комментарии.

API для задач:   
Возможность получения задач конкретного автора или исполнителя, фильтрации и пагинации.

Комментарии: Возможность добавления и просмотра комментариев к задачам.

Валидация данных: Входящие данные проверяются на корректность.

Документация API: API документировано с помощью OpenAPI и Swagger. Swagger UI доступен для тестирования API.

Тестирование: Написаны тесты для проверки методов Task сервиса.

Запуск базы данных PostgreSQL через Docker Compose:  
В корне проекта находится папка docker c файлом docker-compose.yaml. Запустите его командой в терминале: docker-compose up.

Swagger UI:
После успешного запуска приложения Swagger UI будет доступен по адресу: http://localhost:8080/swagger-ui/index.html.
