/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sensorapi.exceptions;

import com.mycompany.sensorapi.models.ErrorMessage;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof javax.ws.rs.WebApplicationException) { // is a http error
            return ((javax.ws.rs.WebApplicationException) exception).getResponse();
        }
        ErrorMessage error = new ErrorMessage( // is most likely a runtime error
                "An internal server error occurred",
                500,
                "https://github.com/Boxcar2122/SensorAPI#error-responses"
        );
        return Response.status(500).entity(error).build();
    }
}
