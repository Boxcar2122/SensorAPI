/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sensorapi.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

import com.mycompany.sensorapi.Database;
import com.mycompany.sensorapi.exceptions.LinkedResourceNotFoundException;
import com.mycompany.sensorapi.models.ErrorMessage;
import com.mycompany.sensorapi.models.Sensor;
import com.mycompany.sensorapi.models.Room;

@Path("/sensors")
public class SensorResource {

    // GET /api/v1/sensors/
    // GET /api/v1/sensors?type={type}
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensors(@QueryParam("type") String type) {
        if (type == null || type.trim().isEmpty()) { // if not using QueryParam
            return Response.ok(Database.sensors).build(); // 200
        }
        List<Sensor> filtered = new ArrayList<>();
        for (Sensor sensor : Database.sensors) { // loop through existing sensors, store room with matching type query
            if (sensor.getType().equalsIgnoreCase(type)) {
                filtered.add(sensor);
            }
        }
        return Response.ok(filtered).build(); // 200
    }

    // GET /api/v1/sensors/{id}
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorById(@PathParam("id") String id) {
        for (Sensor sensor : Database.sensors) { // search through sensors
            if (sensor.getId().equals(id)) { // found sensor with matching id
                return Response.ok(sensor).build(); // 200
            }
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorMessage("Sensor with id '" + id + "' not found", 404, "https://github.com/Boxcar2122/SensorAPI#error-responses"))
                .build();
    }

    // POST /api/v1/sensors/
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postSensor(Sensor sensor) {
        if (sensor == null || sensor.getId() == null || sensor.getId().trim().isEmpty()) { // ensure valid id
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorMessage("Sensor or sensor id must not be null", 400, "https://github.com/Boxcar2122/SensorAPI#error-responses"))
                    .build();
        }
        for (Sensor existingSensor : Database.sensors) { // ensure unique ids
            if (existingSensor.getId().equals(sensor.getId())) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(new ErrorMessage("Sensor with id '" + sensor.getId() + "' already exists", 409, "https://github.com/Boxcar2122/SensorAPI#error-responses"))
                        .build();
            }
        }

        for (Room room : Database.rooms) { // check for room id and create sensor if found
            if (room.getId().equals(sensor.getRoomId())) {
                Database.sensors.add(sensor);
                room.getSensorIds().add(sensor.getId());
                return Response.status(Response.Status.CREATED)
                        .entity(sensor)
                        .build(); // 201
            }
        }
        throw new LinkedResourceNotFoundException("Room with id '" + sensor.getRoomId() + "' does not exist");
    }

    // DELETE /api/v1/sensors/
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSensor(@PathParam("id") String id) {
        Sensor sensorToDelete = null;
        for (Sensor sensor : Database.sensors) { // try to find sensor with given id
            if (sensor.getId().equals(id)) {
                sensorToDelete = sensor; // found
                break;
            }
        }
        if (sensorToDelete == null) { // if not found after searching
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage("Sensor with id '" + id + "' not found", 404, "https://github.com/Boxcar2122/SensorAPI#error-responses"))
                    .build();
        }
        Database.sensors.remove(sensorToDelete); // remove sensor
        for (Room room : Database.rooms) {
            if (room.getId().equals(sensorToDelete.getRoomId())) {
                room.getSensorIds().remove(id);
            }
        }
        return Response.noContent().build(); // 204
    }

    // sub resource locator method: initializes dedicated SensorReadingResource class for handling specific sensor id
    // is for /api/v1/sensors/{sensorId}/readings
    @Path("/{id}/readings")
    public SensorReadingResource getReadingResource(@PathParam("id") String id) {
        return new SensorReadingResource(id);
    }
}
