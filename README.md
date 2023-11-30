# Weather Forecast Application

- **Title:** Weather Forecast Application
- **Subject:** Data science application development
- **Course:** Second
- **Academic Year:** 2023
- **Degree:** Data Science Engineering Data
- **School:** Computer engineering school (EII)
- **University:** Las Palmas de Gran Canaria University (ULPGC)

## Summary of Functionality

This Java project is a simple weather forecast application that fetches weather data from the OpenWeatherMap API and stores it in a SQLite database every 6 hours. The application is designed to retrieve weather information for specified locations at regular intervals and store the data in a local SQLite database.

## Project Structure

The project's architecture is thoughtfully organized into well-defined packages, each serving a specific purpose:

### `org.ulpc.dacd.control`

This package is dedicated to classes responsible for orchestrating the application's flow.

- **`org.ulpgc.dacd.control.Main`**: The main class serves as the entry point, initializing essential components and triggering the weather data update.
- **`OpenWeatherMapSupplier`**: Manages communication with the OpenWeatherMap API, handling requests, and parsing the received data.
- **`SqliteWeatherStore`**: Takes charge of managing the SQLite database, including the storage of weather data.
- **`WeatherController`**: Orchestrates the execution of tasks at regular intervals, ensuring the periodic update of weather data.
- **`WeatherRepository`**: Defines an interface for retrieving weather data, abstracting the data retrieval process.
- **`WeatherSupplier`**: Defines an interface for supplying weather data, allowing for different implementations.

### `org.ulpc.dacd.model`

This package encapsulates classes that vividly represent the data model of the application.

- **`Location`**: A representation of a geographical location, characterized by latitude, longitude, and a distinctive name.
- **`Weather`**: An embodiment of weather data, encompassing information such as temperature, humidity, wind speed, and more.

## Design

### Design Principles and Patterns

The project strictly adheres to fundamental design principles, fostering a robust and maintainable codebase through the following practices:

- **Modularity:** The use of packages (`org.ulpc.dacd.control` and `org.ulpc.dacd.model`) ensures a modular structure, enhancing code organization and readability.
  
- **Separation of Concerns:** Clear segregation between control and model components allows for focused development, making it easier to understand, maintain, and extend specific functionalities.

- **Code Readability:** Emphasis on clean and readable code contributes to a more comprehensible and maintainable project.

### Class Diagram

[Include a link or image of your class diagram here.]

### Relationships and Dependencies

Within the `control` package, classes collaborate seamlessly to orchestrate the application's flow:

- **WeatherController:** Orchestrates tasks, relying on the collaboration of `OpenWeatherMapSupplier` and `SqliteWeatherStore` to fetch and store weather data periodically.

- **OpenWeatherMapSupplier:** Manages communication with the OpenWeatherMap API, ensuring accurate data retrieval.

- **SqliteWeatherStore:** Takes responsibility for managing the SQLite database, storing and updating weather data efficiently.

The `WeatherRepository` and `WeatherSupplier` interfaces define well-defined contracts, establishing clear guidelines for data retrieval and supply. This separation of concerns facilitates flexibility and scalability.



## How to Use

1. Open the `org.ulpgc.dacd.control.Main` class in the `org.ulpc.dacd.control` package.
2. Ensure you have the necessary API key for OpenWeatherMap. You can obtain it [here](https://openweathermap.org/appid).
3. Replace the placeholder API key in the `org.ulpgc.dacd.control.Main` class with your actual API key.
4. Define the locations for which you want to retrieve weather data by creating `Location` objects and passing them to the `WeatherController` instance.
5. Run the `org.ulpgc.dacd.control.Main` class to initiate the weather data retrieval process. The application will update the weather data for the specified locations every 6 hours.

## Resources Used

### Development Environment

- **IDE:** [IntelliJ IDEA](https://www.jetbrains.com/idea/)

### Version Control

- **Git and GitHub:** [Git](https://git-scm.com/) for version control and [GitHub](https://github.com/) for hosting the repository.

### Documentation Tools

- **Markdown:** Used for creating the README.md file.

### Build and Dependency Management

- **Maven:** [Apache Maven](https://maven.apache.org/) for project build and dependency management.

### Libraries

- **Gson:** A Java library for JSON serialization and deserialization. [GitHub - Gson](https://github.com/google/gson)
  
- **SQLite JDBC:** SQLite JDBC driver for database connectivity. [GitHub - SQLite JDBC](https://github.com/xerial/sqlite-jdbc)

- **Logback:** A logging framework for Java applications. [Logback](http://logback.qos.ch/)


## Notes

- The application uses a simple SQLite database to store weather data. The database file is named `weather.db` and is created in the project directory.
- The OpenWeatherMap API is queried for a 5-day weather forecast, and only data for 12:00 PM of each day is extracted and stored.

