import time
import json
import random
import sys

# Check if paho-mqtt client library is available
try:
    import paho.mqtt.client as mqtt
except ImportError:
    print("Error: The 'paho-mqtt' library is missing.")
    print("Please run this command in your terminal first: pip install paho-mqtt")
    sys.exit(1)

# Network configuration routes
MQTT_BROKER = "mosquitto"
MQTT_PORT = 1883
TELEMETRY_TOPIC = "robots/telemetry"

# Instantiate a structural mock robot profile
robot_profile = {
    "robotName": "AMR_01",
    "x": 0.0,
    "y": 0.0,
    "battery": 100,
    "status": "AVAILABLE"
}

# Establish connection to the local Mosquitto message container grid
client = mqtt.Client()
try:
    client.connect(MQTT_BROKER, MQTT_PORT, 60)
    print(f"Robot Simulator started successfully! Connected to FMS Broker at {MQTT_BROKER}:{MQTT_PORT}")
except Exception as e:
    print(f"Failed to connect to MQTT broker: {e}")
    print("Make sure your docker-compose containers are finished building and running.")
    sys.exit(1)

# Infinite loop simulating real-time telemetry updates (1Hz Frequency)
try:
    while True:
        # Simulate slight physical movements over structural warehouse coordinates
        if robot_profile["status"] == "AVAILABLE":
            robot_profile["x"] += round(random.uniform(-0.5, 0.5), 2)
            robot_profile["y"] += round(random.uniform(-0.5, 0.5), 2)

            # Deplete battery metrics slowly over time
            if random.random() > 0.7:
                robot_profile["battery"] -= 1

        # Auto-recovery if battery falls low
        if robot_profile["battery"] <= 15:
            robot_profile["status"] = "CHARGING"
        if robot_profile["status"] == "CHARGING":
            robot_profile["battery"] += 5
            if robot_profile["battery"] >= 100:
                robot_profile["status"] = "AVAILABLE"

        # Serialize structural metrics to a JSON string payload packet
        payload_string = json.dumps(robot_profile)

        # Publish packet across the active integration broker network pipelines
        client.publish(TELEMETRY_TOPIC, payload_string)
        print(f"Telemetry Broadcast sent -> {payload_string}")

        # Wait 1 second (1Hz telemetry standard)
        time.sleep(1)

except KeyboardInterrupt:
    print("\nRobot Simulator shut down cleanly.")
    client.disconnect()
