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
