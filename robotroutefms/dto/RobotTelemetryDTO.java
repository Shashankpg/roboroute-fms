package com.example.robotroutefms.dto;

import lombok.Data;

@Data
public class RobotTelemetryDTO {
    private String robotName;
    private double x;
    private double y;
    private int battery;
    private String status;
}
