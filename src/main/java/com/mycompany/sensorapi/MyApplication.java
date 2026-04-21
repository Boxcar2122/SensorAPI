/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sensorapi;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

import com.mycompany.sensorapi.resources.DiscoveryResource;
import com.mycompany.sensorapi.resources.SensorRoomResource;
import com.mycompany.sensorapi.resources.SensorResource;

import com.mycompany.sensorapi.exceptions.RoomNotEmptyExceptionMapper;
import com.mycompany.sensorapi.exceptions.LinkedResourceNotFoundExceptionMapper;
import com.mycompany.sensorapi.exceptions.SensorUnavailableExceptionMapper;
import com.mycompany.sensorapi.exceptions.GlobalExceptionMapper;

import com.mycompany.sensorapi.logging.LoggingFilter;

@ApplicationPath("/api/v1")
public class MyApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(DiscoveryResource.class);
        classes.add(SensorRoomResource.class);
        classes.add(SensorResource.class);

        classes.add(RoomNotEmptyExceptionMapper.class);
        classes.add(LinkedResourceNotFoundExceptionMapper.class);
        classes.add(SensorUnavailableExceptionMapper.class);
        classes.add(GlobalExceptionMapper.class);

        classes.add(LoggingFilter.class);
        return classes;
    }
}
