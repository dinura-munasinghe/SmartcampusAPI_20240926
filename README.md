Author: Dinura Munasinghe 20240926/W2120158
Module: 5COSC022W Client-Server Architectures

# Smart Campus Sensor and Room Management API

This project is a JAX-RS RESTful API for managing university campus rooms, sensors, and sensor readings.

The API uses Jersey with servlet deployment on Apache Tomcat. Data is stored in memory using Java collections, as required by the coursework.

## Technology

- Java
- Maven
- JAX-RS with Jersey
- Apache Tomcat
- WAR deployment
- In-memory data structures only

## Base URL

After deploying `SmartcampusAPI.war` to Tomcat:

```text
http://localhost:8080/SmartcampusAPI/api/v1
```

## Build and Deploy

1. Build the project:

```bash
mvn clean package
```

2. Deploy this WAR file to Apache Tomcat:

```text
target/SmartcampusAPI.war
```

3. Open the discovery endpoint:

```text
http://localhost:8080/SmartcampusAPI/api/v1
```

## Main Endpoints

```text
GET    /api/v1
GET    /api/v1/rooms
POST   /api/v1/rooms
GET    /api/v1/rooms/{roomId}
DELETE /api/v1/rooms/{roomId}
GET    /api/v1/sensors
GET    /api/v1/sensors?type=CO2
POST   /api/v1/sensors
GET    /api/v1/sensors/{sensorId}
GET    /api/v1/sensors/{sensorId}/readings
POST   /api/v1/sensors/{sensorId}/readings
```

## Sample Curl Commands

Create a room:

```bash
curl -i -X POST http://localhost:8080/SmartcampusAPI/api/v1/rooms -H "Content-Type: application/json" -d "{\"id\":\"LIB-301\",\"name\":\"Library Quiet Study\",\"capacity\":40}"
```

Get all rooms:

```bash
curl -i http://localhost:8080/SmartcampusAPI/api/v1/rooms
```

Create a sensor:

```bash
curl -i -X POST http://localhost:8080/SmartcampusAPI/api/v1/sensors -H "Content-Type: application/json" -d "{\"id\":\"CO2-001\",\"type\":\"CO2\",\"status\":\"ACTIVE\",\"currentValue\":400.0,\"roomId\":\"LIB-301\"}"
```

Filter sensors by type:

```bash
curl -i "http://localhost:8080/SmartcampusAPI/api/v1/sensors?type=CO2"
```

Add a reading:

```bash
curl -i -X POST http://localhost:8080/SmartcampusAPI/api/v1/sensors/CO2-001/readings -H "Content-Type: application/json" -d "{\"value\":415.5}"
```

Try to delete a room that still has sensors:

```bash
curl -i -X DELETE http://localhost:8080/SmartcampusAPI/api/v1/rooms/LIB-301
```

## Report Answers

### Part 1.1: JAX-RS Resource Lifecycle

The default scoping of JAX-RS resource classes is request scoping. In this context, what that means is a new resource instance will usually be created for each request. Since this is the case, any instance variable data that a resource class uses will not survive from one request to another. For this assignment, we have implemented shared in-memory storage for data via static HashMaps in our service classes since all the requests need to share the same data.

### Part 1.2: Discovery Endpoint and HATEOAS

The hypermedia links facilitate the process for clients to find out what APIs are available by looking at the response provided by the API. It is beneficial because clients will not have to hardcode all the endpoints or rely solely on documentation outside the API.

### Part 2.1: Returning IDs vs Full Room Objects

Room ids only require less bandwidth and are helpful if there is no necessity for detailed information at all. Full room objects provide all the necessary data at once and thus eliminate the need to send additional queries. The decision which variant to choose depends on several factors.

### Part 2.2: DELETE Idempotency

DELETE is supposed to be idempotent since executing multiple DELETE commands that are identical should result in an end state on the server. In the current case, executing DELETE on the room deletes it only if the room exists and doesn't have any sensors. Any other DELETE command after that would fail with not found, but the room will still be deleted.

### Part 3.1: @Consumes JSON Mismatch

The `@Consumes(MediaType.APPLICATION_JSON)` this annotation informs the JAX-RS implementation that the POST operation will process a JSON body in the request. If the client passes something other than `application/json`, for example `text/plain`, then JAX-RS can refuse the request and send back an error of "unsupported media type".

### Part 3.2: Query Parameters vs Path Parameters for Filtering

A query parameter is a more effective way to filter collections since the resource in question is the same collection; for example, "/sensors." A path parameter works effectively when a particular resource is identified. A URL like `/sensors?type=CO2` clearly means "show sensors filtered by type".

### Part 4.1: Sub-Resource Locator Pattern

The sub-resource locator pattern helps keep nested API logic separate. `SensorResource` handles sensors, while `SensorReadingResource` handles readings for a specific sensor. This avoids putting every nested path into one very large resource class and makes the API easier to maintain.

### Part 5.2: Why 422 Can Be Better Than 404

HTTP 404 usually means the requested URL resource does not exist. In the sensor creation case, the URL `/sensors` exists, but the JSON body refers to a room that does not exist. Therefore, 422 is more specific because the request format is valid but the payload cannot be processed due to an invalid linked resource.

### Part 5.4: Risk of Exposing Stack Traces

Exposure of Java stack traces can give out the names of classes, packages, versions of libraries, file paths, and other implementation details that attackers can use to know about the system architecture and discover any vulnerabilities in it. A generic JSON error response would be better.

### Part 5.5: Why Use Filters for Logging

It is appropriate to use JAX-RS filters for crosscutting functionalities like logging since they run before and after the resource methods. It will eliminate the need for repeated `Logger.info()` calls in all the endpoints.