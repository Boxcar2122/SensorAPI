# SensorAPI
## Overview
An API designed for overseeing rooms and their linked sensors to track unique readings of varying types.  It has been made as a web service using JAX-RS (Jakarta RESTful Web Services), meaning it uses HTTP methods like GET, POST, and DELETE. It provides an interface for campus facility managers and automates systems that interact with the campus data.

## How to Build and Run
You need Java SDK 17, Apache NetBeans and Apache Tomcat 9 installed and ready.

1. Create a folder where you want the project files
2. Open a terminal in this folder (shift+rightclick)
3. Run `git clone https://github.com/Boxcar2122/SensorAPI.git` which will clone the project to the selected folder
4. Open the project in Apache NetBeans
5. Add Tomcat as a server in NetBeans if you haven't already. Go to Tools > Servers > Add Server > choose Apache Tomcat or TomEE > point it to your extracted Tomcat server folder path. Credentials must be filled in so just put anything there
6. Right click the project (under Projects) and click Run. NetBeans will build the project, start Tomcat, and deploy the app automatically
7. Open `http://localhost:8080/SensorAPI/api/v1` in your browser (you can also do this on Postman)
8. If you make any changes to the code, find Servers under Services, find your Tomcat server and right click it, then click Stop to Run the server again (applies changes)

That should show the discovery resource with the API metadata.

## Error Responses

All errors return a JSON. Example:
```
{
    "errorMessage": "Not found error", // description of the error
    "errorCode": 404, // HTTP error code
    "documentation": "https://github.com/Boxcar2122/SensorAPI#error-responses" // link to this README
}
```
### Status Codes Used
- 200 OK: Successful GET
- 201 Created: Successful POST
- 204 No Content: Successful DELETE
- 400 Bad Request: Missing or invalid fields
- 403 Forbidden: Posting to a MAINTENANCE sensor
- 404 Not Found: Resource does not exist
- 409 Conflict: Duplicate ID or deleting room with sensors
- 422 Unprocessable Entity: Sensor references a non-existent room
- 500 Internal Server Error: Runtime error

## Sample Curl Commands
### 1. Get API metadata (discovery resource)
`curl http://localhost:8080/SensorAPI/api/v1`
### 2. Get all rooms
`curl http://localhost:8080/SensorAPI/api/v1/rooms`
### 3. Create a new room
`curl -X POST http://localhost:8080/SensorAPI/api/v1/rooms -H "Content-Type: application/json" -d "{"id":"LAB-202","name":"Robotics Lab","capacity":25}"`
### 4. Filter sensors by type
`curl "http://localhost:8080/SensorAPI/api/v1/sensors?type=Temperature"`
### 5. Post a new reading to a sensor
`curl -X POST http://localhost:8080/SensorAPI/api/v1/sensors/TEMP-001/readings -H "Content-Type: application/json" -d "{"value":24.7}"`

## Questions Report
**1.1:** In your report, explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures (maps/lists) to prevent data loss or race conditions.
	 Resource class instances are per request and arent a single system that is reused for multiple requests, meaning their lifecycle is limited to per request. Instance variables do not persist between requests which is why the data is stored as static within the database class. This way, every new resource instance (as created per request) can access the same data. However, sometimes there can be race conditions between two requests when they are received by the server at the same time. Using normal ArrayLists or HashMaps can cause desynchronization (not thread-safe), so switching them to CopyOnWriteArrayList and ConcurrentHashMap respectively can prevent this, ensuring synchronization.

**1.2:** Why is the provision of "Hypermedia" (links and navigation within responses) considered a hallmark of advanced RESTful design (HATEOAS)? How does this approach benefit client developers compared to static documentation?
	HATEOAS essentially means an API that supplies you with navigation instead of completely depending on documentation for traversal. It's better like this as it makes it more user friendly and lessens time needed to interact with and understand the system put in place by the API designers. The approach benefits both developers and clients of the API as it essentially acts as self-documentation. By providing URLs to the various segments of the API (as I did in my discovery resource), the client and developer can get a quick idea/reminder of the system. This helps the client for ease of access, and it also helps the developer for testing.

**2.1:** When returning a list of rooms, what are the implications of returning only IDs versus returning the full room objects? Consider network bandwidth and client side processing.
	Let's say we have a large database with thousands of rooms. Returning only IDs would be much lighter on both ends, the server and the client, as they both send and receive less bytes of data. However, with less bytes of data, your data is more narrowed down and less comprehensive, which contradicts the whole point of this API: to manage and oversee rooms, their sensors, and all their readings, alongside necessary data for future decisionmaking and planning. I would say it's okay to have a type of request that *only* gets room IDs if that's all that is needed from the client, but for campus facility managers, just the IDs won't be enough, so it is somewhat justified. 

**2.2:** Is the DELETE operation idempotent in your implementation? Provide a detailed justification by describing what happens if a client mistakenly sends the exact same DELETE request for a room multiple times.
	Idempotency within the DELETE HTTP method essentially means that sending the same DELETE request multiple times does not change the data beyond the first request: the resource is either gone and can't be removed anymore, or was non-existent. In my implementation, every time the DELETE method is successful, it removes that value from the database and from any parenting values that it is connected to. I have also implemented checks and failsafes to ensure a client cannot run a DELETE request on a non-existent ID (incase they ran it once to delete the actual value, and once more with the same ID, which is now non-existent since it was removed already). It will return a 404 not found error description and do nothing, due to the ID simply not existing. Although the status codes are different (204 the first time, 404 after), the state of the server doesn't change beyond the first DELETE request, which is what idempotency requires.

**3.1:** We explicitly use the @Consumes (MediaType.APPLICATION_JSON) annotation on the POST method. Explain the technical consequences if a client attempts to send data in a different format, such as text/plain or application/xml. How does JAX-RS handle this mismatch?
	The @Consumes(MediaType.APPLICATION_JSON) annotation tells JAX-RS that this POST method can only accept requests where the Content-Type is the same as we have signified, being JSON. If the client sends a different type of content like XML or HTML or just raw text, it will get automatically rejected and met with a 415 Unsupported Media Type error, just by the type of the content (the header). 415 means that the URL and request is valid however the content type that was attempted to be POSTed is not matching that of the system. This allows for type checking to be handled by JAX-RS completely, meaning I don't have to manually do type checking in my code to prevent runtime errors. Overall prevents more data transfer issues and misuse from the clientside.

**3.2:** You implemented this filtering using @QueryParam. Contrast this with an alternative design where the type is part of the URL path (e.g., /api/v1/sensors/type/CO2). Why is the query parameter approach generally considered superior for filtering and searching collections?
	Paths (api/v1/sensors/) are generally used to identify resource collections, not for changing request results. Type searching (@QueryParam) allows for filtering and manipulation of what results you get from the specified path when requesting. For example, if there are specific sensors that the facility managers want to see (like CO2 sensors), they can request (/sensors?type=CO2) to make the list of data they receive more precise instead of getting every single sensor. This narrows down what you want to get so the response doesn't bloat network bandwidth and put extra stress on resources when you do not need those excluded results in the first place. It is also more scalable since extra filters can be added later using more parameters (&type=CO2&status=ACTIVE) without having to redesign the URL structure.

**4.1:** Discuss the architectural benefits of the Sub-Resource Locator pattern. How does delegating logic to separate classes help manage complexity in large APIs compared to defining every nested path (e.g., sensors/{id}/readings/{rid}) in one massive controller class?
	The Sub-Resource Locator pattern allows us to separate logic into their own classes instead of having everything in one big class. In my code implementation, the SensorResource class handles everything related to sensors. It also manages requests that are trying to get readings (sensors/{id}/readings) by creating an instance of the SensorReadingResource to handle it. This helps with the complexity of the code by letting each class have their own responsibility. It's good practice to modularize (to an extent) your classes and have them specialized. Generalized classes get messy really quickly, taking away from the human readability. It's also important to think of scalability, as this is a relatively small API project. If bigger API projects need many many nested sub-resources but use a single class to handle it, it gets incredibly bloated with unrelated methods. Also with this method, the sensor ID is received once by the locator method and passed into the sub-resource class as a parameter, so you don't have to retrieve it again in every nested method.

**5.2:** Why is HTTP 422 often considered more semantically accurate than a standard 404 when the issue is a missing reference inside a valid JSON payload?
	The resource we're searching through exists, but the *linked resource* doesn't. A 404 error signifies that the path has nothing existing to return as a response, so in this case it would be misleading, since it would imply that the (/sensors) resource doesn't exist, when the actual issue is that the JSON we're trying to use the POST method on references a room ID that doesn't exist in the code's database. 422 Unprocessable Entity is more semantically accurate here because it means the server understood what we're trying to do, POST a JSON in (/sensors) which is in correct format too and has the right Content-Type, however it throws an exception since the JSON we are POSTing has an issue with its content. In this case, it's the room ID that we're trying to reference. This makes it so debugging is less vague, so the client knows it's nothing to do with the URL or file type they're trying to POST, but the content.

**5.4:** From a cybersecurity standpoint, explain the risks associated with exposing internal Java stack traces to external API consumers. What specific information could an attacker gather from such a trace?
	By letting the raw Java stack traces show when an error occurs, it gives detailed information on how the backend works which makes security a potential issue. The detailed information it gives contains things like class names, line numbers,  and structure of the application behind the scenes, like (com.mycompany.sensorapi.resources.SensorResource). It also shows libraries used and their versions which obviously can be looked up and researched for vulnerabilities. This is why using (GlobalExceptionMapper) is beneficial, as any possible error that happens internally will only be revealed as a 500 Internal Server Error response. The actual exception details are logged for developers to view and work on.

**5.5:** Why is it advantageous to use JAX-RS filters for cross-cutting concerns like logging, rather than manually inserting Logger.info() statements inside every single resource method?
	Using JAX-RS filters for logging instead of manually logging with Logger.info() helps keep all logging logic in one place. This way, logging applies to every single request made from the clientside and response given from the serverside equally, meaning that it doesn't need to be in separate methods. It works across the entire API. If I had to manually log inside every resource method, I would have to repeat the same code over and over which can lead to inconsistencies. It's similar to making code modular so you Don't Repeat Yourself. This also means that if I wanted to change the log format later, I can do it by changing one method, not all the Logger.info() methods used across the entire codebase. It's also easy to forget to write that Logger.info() somewhere when you need it to, which further increases inconsistencies. The LoggingFilter class I used implements (ContainerRequestFilter) and (ContainerResponseFilter), which is applied to every request and response automatically by JAX-RS, so I can keep my code clean of random log lines. 
