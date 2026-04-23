# SensorAPI
## Overview
An API designed for overseeing rooms and their linked sensors to track unique readings of varying types.  It has been made as a web service using JAX-RS (Jakarta RESTful Web Services), meaning it uses HTTP methods like GET, POST, and DELETE. It provides a seamless interface for campus facility managers and makes managing systems that interact with the campus data much less manual

## How to Build and Run
You need Java SDK 17, Apache NetBeans and Apache Tomcat 9 installed and ready.

1. Create a folder where you want the project files
2. Open a terminal in this folder (shift+rightclick)
3. Run `git clone https://github.com/Boxcar2122/SensorAPI.git` which will clone the project to the selected folder
4. Open the project in Apache NetBeans
5. Add Tomcat as a server in NetBeans if you haven't already (Tools>Servers>Add Server>Apache Tomcat or TomEE>point it to your Tomcat folder with credentials too)
	- If you don't have a Tomcat server, download it [here](https://github.com/Boxcar2122/SensorAPI/blob/main/apache-tomcat-9.0.100.zip)
6. Right click the project (under Projects) and click Run. NetBeans will build the project, start Tomcat, and deploy the app automatically.
7. Open `http://localhost:8080/SensorAPI/api/v1` in your browser (you can also do this on Postman)
8. If you make any changes to the code, find Servers under Services, find your Tomcat server and right click it, then click Stop to Run the server again (applies changes)

That should show the discovery endpoint with the API metadata.
## Error Responses

All errors return a JSON. Example:
```json
{
    "errorMessage": "description of the error",
    "errorCode": "HTTP status code",
    "documentation": "Link to this README"
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