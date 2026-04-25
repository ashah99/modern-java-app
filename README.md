# Modern Java Contact Manager
**Stack:** Java 17, Spring Boot 3, JavaFX, Hibernate, H2.

## Key Features
* **Bootstrap Pattern:** Using a non-JavaFX class to launch the app avoids module errors.
* **Spring Integration:** Using `ConfigurableApplicationContext` to bridge the Spring backend with the JavaFX frontend.
* **Data Binding:** Using `ObservableList` and `PropertyValueFactory` to sync the Database with the UI Table.
* **H2 Access:** Forcing `WebApplicationType.SERVLET` to keep the H2 web console reachable at localhost:8080.