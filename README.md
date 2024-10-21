Для запуска приложения необходимо сначала
поднять docker-compose, после запусть сервис 
EurekaServiceApplication, а затем
EmployeeService и DepartmentService.

localhost:8080/swagger-ui/index.html - тестирования сервиса отделов через Swagger-UI
localhost:8081/swagger-ui/index.html - тестирование сервиса сотрудников

*** 
### Сборка приложения
#### Для отделов: 

в department-service запустить <code>mvn clean install</code>

#### Для работников

в employee-service запустить <code>mvn clean install</code>

***
Для проверки Jacoco можно увеличить параметр <code>jacoco-coverage-ratio</code> в pom.xml файлке
нужного модуля

***
### Проверка метрик через Grafana

localhost:3000 - подключение к Grafana. Логин и пароль - admin.

В файле config/prometheus/prometheus.yaml в <code>targets</code> на месте хоста нужно указать локальный IP.

Для визуализации метрик JVM и CPU использовался готовый дашборд: https://github.com/IlyushaShulenin/dashboard.git

#### Отчеты по собранным метрикам

Использование CPU

<img src="imgs/cpu_usage.png">

JVM

<img src="imgs/jvm_1.png">
<img src="imgs/jvm_2.png">

Работа GC

<img src="imgs/gc.png">

Время выполнение запроса по добавлению новго сотрудника в миллисекундах

<img src="imgs/saving_time.png">

***

# Отчет по 20 задаче

* Шаг 1-2 - установка Jenkins

<img src="imgs/1-2-Jenkins-installing.png">

* Шаг 3 - установка JDK и Maven

<img src="imgs/3-JDK-already-installed.png">

<img src="imgs/3-Maven-already-installed.png">

* Шаг 4 - конфигурация JDK и Maven

<img src="imgs/4-JDK-config.png">

* Шаг 5 - установка необходимых плагинов

<img src="imgs/5-installing-plugins .png">

* Шаг 6 - конфигурация прав пользователей

<img src="imgs/6-role-plugin.png">

<img src="imgs/6-role-config.png">

* Шаг 7 - создание job-ы

<img src="imgs/7-created-job.png">

* Шаг 8 - добавление build trigger и build action

<img src="imgs/8-trigger-config-build-every-15-minutes.png">

<img src="imgs/8-build-action-config.png">

<img src="imgs/8-build-action-output.png">

* Шаг 9 - отправка сообщений о билдах

<img src="imgs/9-mail-notification-config.png">

<img src="imgs/9-notification-message.png">