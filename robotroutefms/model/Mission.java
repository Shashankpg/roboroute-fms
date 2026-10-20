package com.example.robotroutefms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "missions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assigned_robot_id")
    private Robot assignedRobot; // The AMR picked to execute this task

    @Column(nullable = false)
    private String status; // PENDING, EN_ROUTE_TO_PICKUP, EXECUTING, COMPLETED, FAILED

    private double pickupX;
    private double pickupY;

    private double dropX;
    private double dropY;

    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
