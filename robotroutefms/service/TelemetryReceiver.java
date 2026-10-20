package com.example.robotroutefms.service;

import com.example.robotroutefms.dto.RobotTelemetryDTO;
import com.example.robotroutefms.model.Robot;
import com.example.robotroutefms.repository.RobotRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TelemetryReceiver implements MessageHandler {

    @Autowired
    private RobotRepository robotRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) throws MessagingException {
        String payload = (String) message.getPayload();
        try {
            // Convert JSON string to DTO
            RobotTelemetryDTO telemetry = objectMapper.readValue(payload, RobotTelemetryDTO.class);
            
            // Check if robot exists, update it; otherwise create a new entry
            Optional<Robot> existingRobot = robotRepository.findByRobotName(telemetry.getRobotName());
            Robot robot = existingRobot.orElseGet(() -> {
                Robot newRobot = new Robot();
                newRobot.setRobotName(telemetry.getRobotName());
                return newRobot;
            });

            robot.setPositionX(telemetry.getX());
            robot.setPositionY(telemetry.getY());
            robot.setBatteryPercentage(telemetry.getBattery());
            robot.setStatus(telemetry.getStatus());

            robotRepository.save(robot);
            System.out.println("Updated status for robot: " + robot.getRobotName() + " via MQTT Telemetry.");

        } catch (Exception e) {
            System.err.println("Failed to parse telemetry payload: " + e.getMessage());
        }
    }
}
