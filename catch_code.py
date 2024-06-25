import json
import random
import uuid
from datetime import datetime, timedelta

# Define the start and end time for the measurements
start_time = datetime(2024, 1, 1, 0, 0)
end_time = datetime(2024, 1, 2, 23, 59)

# Function to generate a random timestamp within the given time frame
def random_timestamp(start, end):
    delta = end - start
    random_seconds = random.randint(0, int(delta.total_seconds()))
    return start + timedelta(seconds=random_seconds)

# Function to generate a unique deploymentId
def generate_deployment_id():
    return str(uuid.uuid4())

# Function to generate location measurements
def generate_location_measurements(start, end):
    measurements = []
    current_time = start
    while current_time <= end:
        measurements.append({
            "sensorStartTime": int(current_time.timestamp() * 1e6),
            "data": {
                "__type": "dk.cachet.carp.location",
                "latitude": random.uniform(-90, 90),
                "longitude": random.uniform(-180, 180),
                "altitude": random.uniform(0, 100),
                "accuracy": random.uniform(0, 50),
                "verticalAccuracy": random.uniform(0, 1),
                "speed": random.uniform(0, 10),
                "speedAccuracy": random.uniform(0, 1),
                "heading": random.uniform(0, 360),
                "headingAccuracy": random.uniform(0, 1),
                "time": current_time.isoformat(),
                "isMock": False,
                "elapsedRealtimeNanos": random.uniform(0, 1e9),
                "elapsedRealtimeUncertaintyNanos": 0
            }
        })
        current_time += timedelta(hours=1)
    return measurements

# Function to generate Polar HR measurements
def generate_polar_hr_measurements(start, end):
    measurements = []
    current_time = start
    while current_time <= end:
        measurements.append({
            "sensorStartTime": int(current_time.timestamp() * 1e6),
            "data": {
                "__type": "dk.cachet.carp.polar.hr",
                "samples": [{
                    "hr": random.randint(60, 100),
                    "rrsMs": [],
                    "contactStatus": True,
                    "contactStatusSupported": True
                }]
            }
        })
        current_time += timedelta(minutes=1)
    return measurements

# Function to generate step count measurements
def generate_step_count_measurements(start, end):
    measurements = []
    current_time = start
    while current_time <= end:
        measurements.append({
            "sensorStartTime": int(current_time.timestamp() * 1e6),
            "data": {
                "__type": "dk.cachet.carp.stepcount",
                "steps": random.randint(0, 100)
            }
        })
        current_time += timedelta(hours=4)
    return measurements

# Updated function to generate data streams with trigger IDs
def generate_data_stream(deployment_id, device_role_name, data_type, measurements, trigger_id):
    return {
        "dataStream": {
            "studyDeploymentId": deployment_id,
            "deviceRoleName": device_role_name,
            "dataType": data_type
        },
        "firstSequenceId": 1,
        "measurements": measurements,
        "triggerIds": [trigger_id]
    }

# Define trigger IDs for each measurement type
location_trigger_id = 7
polar_hr_trigger_id = 5
step_count_trigger_id = 6

# Generate 10 JSON files with the updated structure including trigger IDs
json_files = []
missing_data_info = []

for i in range(10):
    deployment_id = generate_deployment_id()
    location_measurements = generate_location_measurements(start_time, end_time)
    polar_hr_measurements = generate_polar_hr_measurements(start_time, end_time)
    step_count_measurements = generate_step_count_measurements(start_time, end_time)

    # Randomly remove some data to create missing data
    random.shuffle(location_measurements)
    random.shuffle(polar_hr_measurements)
    random.shuffle(step_count_measurements)

    missing_location = random.randint(0, len(location_measurements) // 2)
    missing_polar_hr = random.randint(0, len(polar_hr_measurements) // 2)
    missing_step_count = random.randint(0, len(step_count_measurements) // 2)

    location_measurements = location_measurements[missing_location:]
    polar_hr_measurements = polar_hr_measurements[missing_polar_hr:]
    step_count_measurements = step_count_measurements[missing_step_count:]

    missing_data_info.append({
        "file": f"file_{i+1}.json",
        "missing_location_measurements": missing_location,
        "missing_polar_hr_measurements": missing_polar_hr,
        "missing_step_count_measurements": missing_step_count
    })

    file_content = [
        generate_data_stream(deployment_id, "Location Service", "dk.cachet.carp.location", location_measurements, location_trigger_id),
        generate_data_stream(deployment_id, "Primary Phone", "dk.cachet.carp.polar.hr", polar_hr_measurements, polar_hr_trigger_id),
        generate_data_stream(deployment_id, "Primary Phone", "dk.cachet.carp.stepcount", step_count_measurements, step_count_trigger_id)
    ]

    json_files.append(file_content)

    with open(f"/mnt/data/file_{i+1}.json", "w") as outfile:
        json.dump(file_content, outfile)

# Display missing data info
missing_data_df = pd.DataFrame(missing_data_info)