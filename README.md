# Weather Forecast Application

- **Title:** Weather Forecast Application
- **Subject:** Data science application development
- **Course:** Second
- **Academic Year:** 2023
- **Degree:** Data Science Engineering Data
- **School:** Computer engineering school (EII)
- **University:** Las Palmas de Gran Canaria University (ULPGC)

## Summary of Functionality
This Java project is a comprehensive weather forecast application that seamlessly integrates two modules: Weather Provider and Weather Store. The Weather Provider module fetches weather data from the OpenWeatherMap API, processes it, and sends it to a message broker (Apache ActiveMQ). On the other hand, the Weather Store module subscribes to these weather events, stores them locally, and provides a mechanism to access historical weather data. The application operates in a periodic manner, updating weather data for specified locations every 6 hours.

## How to run .jar files
1. First open a terminal and start the activemq execution.
2. Then start the weather store execution in another terminal with this command: java -jar (address of the .jar).
3. Finally, start the execution of the weather supplier in another terminal with this command: java -jar (address of the .jar) (your apiKey).
## Project Structure
The project's architecture is thoughtfully organized into well-defined packages, each serving a specific purpose:

### Weather Provider Module
- **Control Package (org.ulpc.dacd.control):**
  - `Main:` Entry point of the module. Initializes key components and triggers the weather data update.
  - `OpenWeatherMapSupplier:` Communicates with the OpenWeatherMap API, retrieves, and parses weather data.
  - `JmsWeatherStore:` Connects to the message broker (ActiveMQ) and sends weather data.
  - `WeatherController:` Orchestrates the execution of tasks at regular intervals, ensuring the periodic update of weather data.
  - `WeatherRepository:` Defines an interface for sending weather data to the message broker.
  - `WeatherSupplier:` Interface for fetching weather data based on location and timestamp.

- **Model Package (org.ulpc.dacd.model):**
  - `Weather:` Represents weather data, including information such as precipitation probability, wind speed, temperature, humidity, and more.
  - `Location:` Represents a geographical location with latitude, longitude, and a name.

### Weather Store Module
- **Control Package (org.ulpgc.dacd.control):**
  - `Main:` Entry point of the module. Initializes key components and triggers the weather data storage process.
  - `EventStore:` Responsible for storing weather events locally.
  - `TopicSubscriber:` Subscribes to weather prediction messages from the `prediction.weather` topic and stores the events using the `EventStore`.
  - `Subscriber:` Interface defining the contract for classes that subscribe to messages.
  - `EventStoreBuilder:` Interface defining the contract for classes that store weather events.

## Design
The project strictly adheres to fundamental design principles, emphasizing modularity, separation of concerns, and code readability. Each module has its well-defined responsibilities, contributing to a robust and maintainable codebase.

### Design Principles and Patterns
- **Modularity:** The codebase is structured into packages, enhancing code organization and maintainability for both the Weather Provider and Weather Store modules.

- **Separation of Concerns:** Clear separation between control and model components in each module allows for focused development, making it easier to understand, maintain, and extend specific functionalities.

- **Code Readability:** The project places a strong emphasis on clean and readable code, contributing to a more comprehensible and maintainable codebase.

### Class Diagram
![Texto alternativo](weather_suplier.jpg)
![Texto alternativo](weather_store.jpg)

### Relationships and Dependencies
The relationship between the two modules is established through the message broker (ActiveMQ), allowing seamless communication and data flow.

## How to Use
1. Open the `Main` class in the `org.ulpc.dacd.control` package of the Weather Provider module.
2. Ensure you have a valid API key for OpenWeatherMap. Obtain it [here](#).
3. Define the locations for which you want to retrieve weather data by creating `Location` objects.
4. Open the `Main` class in the `org.ulpgc.dacd.control` package of the Weather Store module.
5. Ensure that Apache ActiveMQ is running and configured with the `prediction.weather` topic.
6. Run the `Main` class to initiate the message subscription process. The application will store weather events locally.
7. Run the `Main` class to initiate the weather data retrieval process. The application will update the weather data for the specified locations every 6 hours. When you run main you must enter the apikey as an argument.

## Resources Used
### Libraries
- **Gson:** A Java library for JSON serialization and deserialization.
- **ActiveMQ:** Apache ActiveMQ for message subscription and handling.

## Acknowledgments
The Weather Forecast Application leverages the power of two modules, seamlessly providing a comprehensive weather forecast solution. The modular design, adherence to design principles, and integration with external libraries contribute to the project's success.

