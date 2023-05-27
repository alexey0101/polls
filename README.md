# polls

Данный сервис предоставляет API для создания опросов с "ветвистой" схемой.

Для того чтобы запустить сервис необходимо:

1) Указать в application.properties данные для подключения к БД, генерации JWT токенов, порт запуска сервиса
2) Перейти в корневую папку и запустить mvn clean package
3) Перейти в target и запустить полученный jar файл командой java -jar *filename*

Для запуска уже готового JAR файла необходимо открыть командную строку и выполнить команду: java -jar *название jar файла* --spring.jpa.hibernate.ddl-auto=update --spring.datasource.url=*url для подключения к базе данных* --spring.datasource.username=*имя пользователя для доступа к базе данных* --spring.datasource.password=*пароль пользователя для доступа к базе данных* --spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect --server.port=*порт, на котором необходимо запустить приложение* --application.security.jwt.expiration=*время истечения JWT токена* --application.security.jwt.secret-key=*секретный ключ для генерации/валидации JWT токена* --spring.jpa.properties.hibernate.jdbc.time_zone=UTC
