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
import com.mycompany.sensorapi.exceptions.SensorUnavailableException;
import com.mycompany.sensorapi.models.ErrorMessage;
import com.mycompany.sensorapi.models.Sensor;
import com.mycompany.sensorapi.models.SensorReading;

public class SensorReadingResource {

    private final String id;

    public SensorReadingResource(String id) {
        this.id = id;
    }

    // GET /api/v1/sensors/{sensorId}/readings
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getReadings() {
        for (Sensor sensor : Database.sensors) { // search through sensors
            if (sensor.getId().equals(id)) { // found sensor with matching id
                List<SensorReading> sensorReadings = Database.readings.get(id); // get value (readings list) from key (sensor id)
                if (sensorReadings == null) { // if key or value doesnt exist
                    sensorReadings = new ArrayList<>(); // empty readings (not 404 error, it's found but is empty)
                }
                return Response.ok(sensorReadings).build(); // 200
            }
        };
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorMessage("Sensor with id '" + id + "' not found", 404, "https://github.com/Boxcar2122/SensorAPI#error-responses"))
                .build();
    }

    // POST /api/v1/sensors/{sensorId}/readings
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        Sensor existingSensor = null;
        for (Sensor sensor : Database.sensors) { // try to find sensor with given id
            if (sensor.getId().equals(id)) {
                existingSensor = sensor; // found
                break;
            }
        }
        if (existingSensor == null) { // if not found after searching
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage("Sensor with id '" + id + "' not found", 404, "https://github.com/Boxcar2122/SensorAPI#error-responses"))
                    .build();
        }
        if (existingSensor.getStatus().equalsIgnoreCase("MAINTENANCE")) {
            throw new SensorUnavailableException("Sensor '" + id + "' is under maintenance and cannot accept new readings");
        }

        if (reading.getId() == null || reading.getId().trim().isEmpty()) { // if no reading id provided
            reading.setId(java.util.UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0) { // if no timestamp provided
            reading.setTimestamp(System.currentTimeMillis()); // epoch time (ms)
        }
        if (!Database.readings.containsKey(id)) { // if sensor doesnt have readings list (first reading)
            Database.readings.put(id, new ArrayList<>());
        }
        Database.readings.get(id).add(reading); // add reading to sensor's new readings list
        existingSensor.setCurrentValue(reading.getValue()); // update sensor current reading
        return Response.status(Response.Status.CREATED)
                .entity(reading)
                .build(); // 201
    }
}
