# Weather Forecast Application

This Java project is a simple weather forecast application that fetches weather data from the OpenWeatherMap API and stores it in a SQLite database every 6 hours. The application is designed to retrieve weather information for specified locations at regular intervals and store the data in a local SQLite database.

## Project Structure

The project is organized into distinct packages, each serving a specific purpose:

org.ulpc.dacd.control
This package is dedicated to classes responsible for controlling the flow of the application.

Main: The main class that serves as the entry point, initializing essential components, and triggering the weather data update.
OpenWeatherMapSupplier: Manages communication with the OpenWeatherMap API, handling requests, and parsing the received data.
SqliteWeatherStore: Responsible for managing the SQLite database, including storing weather data.
WeatherController: Orchestrates the execution of tasks at regular intervals, ensuring the periodic update of weather data.
WeatherRepository: Defines an interface for retrieving weather data, abstracting the data retrieval process.
WeatherSupplier: Defines an interface for supplying weather data, allowing for different implementations.
org.ulpc.dacd.model
This package contains classes that represent the data model of the application.

Location: Represents a geographical location, characterized by latitude, longitude, and a name.
Weather: Represents weather data, encompassing information such as temperature, humidity, wind speed, etc.

## How to Use

1. Open the `Main` class in the `org.ulpc.dacd.control` package.
2. Ensure you have the necessary API key for OpenWeatherMap. You can obtain it [here](https://openweathermap.org/appid).
3. Replace the placeholder API key in the `Main` class with your actual API key.
4. Define the locations for which you want to retrieve weather data by creating `Location` objects and passing them to the `WeatherController` instance.
5. Run the `Main` class to initiate the weather data retrieval process. The application will update the weather data for the specified locations every 6 hours.

## Dependencies

- [Gson](https://github.com/google/gson): A Java library for JSON serialization and deserialization.
- [SQLite JDBC](https://github.com/xerial/sqlite-jdbc): SQLite JDBC driver for database connectivity.
- Logback: A logging framework for Java applications.

## Notes

- The application uses a simple SQLite database to store weather data. The database file is named `weather.db` and is created in the project directory.
- The OpenWeatherMap API is queried for a 5-day weather forecast, and only data for 12:00 PM of each day is extracted and stored.
- The project uses Java 8's `java.time` package for handling date and time.

Feel free to customize the application based on your needs and integrate additional features or improvements.
