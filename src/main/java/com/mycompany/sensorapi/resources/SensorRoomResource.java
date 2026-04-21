/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sensorapi.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.mycompany.sensorapi.Database;
import com.mycompany.sensorapi.exceptions.RoomNotEmptyException;
import com.mycompany.sensorapi.models.ErrorMessage;
import com.mycompany.sensorapi.models.Room;

@Path("/rooms")
public class SensorRoomResource {

    // GET /api/v1/rooms/
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRooms() {
        return Response.ok(Database.rooms).build(); // 200
    }

    // GET /api/v1/rooms/{id}
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("id") String id) {
        for (Room room : Database.rooms) { // search through rooms
            if (room.getId().equals(id)) { // found room with matching id
                return Response.ok(room).build(); // 200
            }
        }
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorMessage("Room with id '" + id + "' not found", 404, "https://github.com/Boxcar2122/SensorAPI#error-responses"))
                .build();
    }

    // POST /api/v1/rooms/
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postRoom(Room room) {
        if (room == null || room.getId() == null || room.getId().trim().isEmpty()) { // ensure valid id
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorMessage("Room or room ID cannot be null or empty", 400, "https://github.com/Boxcar2122/SensorAPI#error-responses"))
                    .build();
        }
        for (Room existingRoom : Database.rooms) { // ensure unique ids
            if (existingRoom.getId().equals(room.getId())) {
                return Response.status(Response.Status.CONFLICT)
                        .entity(new ErrorMessage("Room with id '" + room.getId() + "' already exists", 409, "https://github.com/Boxcar2122/SensorAPI#error-responses"))
                        .build();
            }
        }
        if (!room.getSensorIds().isEmpty()) { // ensure sensor ids are handled separately
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorMessage("Room posting must not contain predefined sensor ids", 400, "https://github.com/Boxcar2122/SensorAPI#error-responses"))
                    .build();
        }
        Database.rooms.add(room);
        return Response.status(Response.Status.CREATED)
                .entity(room)
                .build(); // 201
    }

    // DELETE /api/v1/rooms/{id}
    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("id") String id) {
        Room roomToDelete = null;
        for (Room room : Database.rooms) { // try to find room with given id
            if (room.getId().equals(id)) {
                roomToDelete = room; // found
                break;
            }
        }
        if (roomToDelete == null) { // if not found after searching
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(new ErrorMessage("Room with id '" + id + "' not found", 404, "https://github.com/Boxcar2122/SensorAPI#error-responses"))
                    .build();
        }

        if (!roomToDelete.getSensorIds().isEmpty()) { // room has existing sensors
            throw new RoomNotEmptyException("Cannot delete room '" + id + "' as it is currently occupied by active hardware");
        }

        Database.rooms.remove(roomToDelete); // remove room
        return Response.noContent().build(); // 204
    }
}
