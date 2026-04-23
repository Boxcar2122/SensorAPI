/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sensorapi;

import com.mycompany.sensorapi.models.Room;
import com.mycompany.sensorapi.models.Sensor;
import com.mycompany.sensorapi.models.SensorReading;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Database {

    public static List<Room> rooms = new CopyOnWriteArrayList<>();
    public static List<Sensor> sensors = new CopyOnWriteArrayList<>();
    public static Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>(); // readings map is assigned sensor id as key, list of readings as value

    static {
        Room lib = new Room("LIB-301", "Library Quiet Study", 30);
        lib.getSensorIds().add("TEMP-001");
        Room lab = new Room("LAB-101", "Computer Lab", 20);
        lab.getSensorIds().add("CO2-001");
        Room lec = new Room("LEC-201", "Lecture Hall", 100);
        lec.getSensorIds().add("OCC-001");
        rooms.add(lib);
        rooms.add(lab);
        rooms.add(lec);

        sensors.add(new Sensor("TEMP-001", "Temperature", "ACTIVE", 22.5, "LIB-301"));
        sensors.add(new Sensor("CO2-001", "CO2", "ACTIVE", 412.0, "LAB-101"));
        sensors.add(new Sensor("OCC-001", "Occupancy", "MAINTENANCE", 0.0, "LEC-201"));

        List<SensorReading> tempReadings = new CopyOnWriteArrayList<>();
        tempReadings.add(new SensorReading(22.5));
        readings.put("TEMP-001", tempReadings);

        List<SensorReading> co2Readings = new CopyOnWriteArrayList<>();
        co2Readings.add(new SensorReading(400.0)); // ppm
        readings.put("CO2-001", co2Readings);

        List<SensorReading> occReadings = new CopyOnWriteArrayList<>();
        occReadings.add(new SensorReading(0.0));
        readings.put("OCC-001", occReadings);
    }
}
