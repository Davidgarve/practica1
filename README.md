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

# Weather Provider Module

## Overview

The Weather Provider module is responsible for fetching weather data from the OpenWeatherMap API, processing it, and storing the relevant information in a message broker (Apache ActiveMQ). This module is designed to operate in a periodic manner, updating weather data for specified locations every 6 hours.

## Project Structure

The module is organized into two packages:

### Control Package (`org.ulpc.dacd.control`)

This package manages the orchestration of the application and handles external communications.

- **Main:** Entry point of the module. Initializes key components and triggers the weather data update.
- **OpenWeatherMapSupplier:** Communicates with the OpenWeatherMap API, retrieves and parses weather data.
- **JmsWeatherStore:** Connects to the message broker (ActiveMQ) and sends weather data.
- **WeatherController:** Orchestrates the execution of tasks at regular intervals, ensuring the periodic update of weather data.
- **WeatherRepository:** Defines an interface for sending weather data to the message broker.
- **WeatherSupplier:** Interface for fetching weather data based on location and timestamp.

### Model Package (`org.ulpc.dacd.model`)

This package encapsulates classes representing the data model of the application.

- **Weather:** Represents weather data, including information such as precipitation probability, wind speed, temperature, humidity, and more.
- **Location:** Represents a geographical location with latitude, longitude, and a name.

## Design

#### Design Principles and Patterns

The Weather Provider module is built on fundamental design principles, emphasizing a robust and maintainable codebase. The following design practices are strictly adhered to:

#### Modularity

The project is structured with modularity in mind, utilizing packages (`org.ulpc.dacd.control` and `org.ulpc.dacd.model`) to create a modular architecture. This enhances code organization, readability, and maintainability.

#### Separation of Concerns

Clear separation between control and model components is a key design consideration. This approach allows for focused development, making it easier to understand, maintain, and extend specific functionalities.

#### Code Readability

The project places a strong emphasis on clean and readable code. This practice contributes to a more comprehensible and maintainable codebase.

### Class Diagram

![Class Diagram](uml.png)

### Relationships and Dependencies

Within the `control` package, classes collaborate seamlessly to orchestrate the application's flow:

- **WeatherController:** Orchestrates tasks, relying on the collaboration of `OpenWeatherMapSupplier` and `JmsWeatherStore` to fetch and store weather data periodically.

- **OpenWeatherMapSupplier:** Manages communication with the OpenWeatherMap API, ensuring accurate data retrieval.

- **JmsWeatherStore:** Takes responsibility for connecting to the message broker (ActiveMQ) and sending weather data.

The `WeatherRepository` and `WeatherSupplier` interfaces define well-defined contracts, establishing clear guidelines for data retrieval and supply. This separation of concerns facilitates flexibility and scalability.

## How to Use

1. Open the `Main` class in the `org.ulpc.dacd.control` package.
2. Ensure you have a valid API key for OpenWeatherMap. Obtain it [here](https://openweathermap.org/appid).
3. Define the locations for which you want to retrieve weather data by creating `Location` objects.
4. When you run the application you must specify what your apikey is in the terminal.
5. Run the `Main` class to initiate the weather data retrieval process. The application will update the weather data for the specified locations every 6 hours.


## Resources used

### Libraries

- [Gson](https://github.com/google/gson): A Java library for JSON serialization and deserialization.

## Notes

- You can modify the `brokerURL` and `topicName` variables in the `Main` class to configure the message broker connection, if you want.
- The OpenWeatherMap API is queried for a 5-day weather forecast, and only data for 12:00 PM of each day is extracted and stored.

# Weather Store Module

## Overview

The Weather Store module is designed to subscribe to weather prediction messages from the prediction.weather topic, store these events locally, and provide a mechanism to access historical weather data. This module integrates with Apache ActiveMQ for message subscription.
## Project Structure

The module is organized into a control package:

### Control Package (`org.ulpc.dacd.control`)

This package manages the orchestration of the application and handles external communications.

- **Main:** Entry point of the module. Initializes key components and triggers the weather data storage process.
- **TopicSubscriber:** Subscribes to weather prediction messages from the prediction.weather topic and stores the events using the EventStore.
- **EventStore:** Responsible for storing weather events locally.
- **Subscriber:** Interface defining the contract for classes that subscribe to messages.
- **EventStoreBuilder:** Interface defining the contract for classes that store weather events.

## Design

#### Design Principles and Patterns

The Weather Store module adheres to fundamental design principles, promoting a robust and maintainable codebase. Key design practices include:

#### Modularity

The codebase is organized into packages, enhancing code organization and maintainability.

#### Separation of Concerns

Clear separation between control components allows for focused development, making it easier to understand and extend specific functionalities.

#### Code Readability

Emphasis on clean and readable code contributes to a more comprehensible and maintainable project.
### Class Diagram

![Class Diagram](uml.png)

### Relationships and Dependencies

Within the control package, classes collaborate seamlessly:

- **TopicSubscriber:** Orchestrates message subscription, relying on the collaboration with EventStore to store weather events.

- **EventStore:** Manages the storage of weather events locally.

## How to Use

1. Open the Main class in the org.ulpgc.dacd.control package.
2. Ensure that Apache ActiveMQ is running and configured with the prediction.weather topic.
3. Run the Main class to initiate the message subscription process. The application will store weather events locally.
