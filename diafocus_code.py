import json
import random
from datetime import datetime, timedelta

# Define the new start and end time for the measurements
start_time = datetime(2024, 1, 1, 15, 0)
end_time = datetime(2024, 7, 1, 14, 59)


# Function to generate a unique deploymentId
def generate_deployment_id():
    return str(uuid.uuid4())


# Function to generate step count measurements
def generate_step_count_measurements(start, end):
    measurements = []
    current_time = start
    while current_time <= end:
        measurements.append({
            "sensorStartTime": int(current_time.timestamp() * 1e6),
            "data": {
                "__type": "dk.cachet.carp.stepcount",
                "steps": random.randint(0, 10000)  # Assuming a more realistic daily step count range
            }
        })
        current_time += timedelta(days=1)
    return measurements


# Function to generate WHO-5 survey measurements
def generate_who5_survey_measurements(start, end):
    measurements = []
    current_time = start
    while current_time <= end:
        task_result = {
            "identifier": str(uuid.uuid4()),
            "startDate": current_time.isoformat(),
            "endDate": (current_time + timedelta(minutes=5)).isoformat(),
            "results": {
                "step1": {
                    "identifier": "step1",
                    "startDate": current_time.isoformat(),
                    "endDate": (current_time + timedelta(minutes=1)).isoformat(),
                    "questionTitle": "I have felt cheerful and in good spirits",
                    "results": {"answer": random.randint(0, 5)},
                    "answerFormat": {
                        "minValue": 0,
                        "maxValue": 5,
                        "suffix": None,
                        "questionType": "Integer"
                    }
                },
                "step2": {
                    "identifier": "step2",
                    "startDate": (current_time + timedelta(minutes=1)).isoformat(),
                    "endDate": (current_time + timedelta(minutes=2)).isoformat(),
                    "questionTitle": "I have felt calm and relaxed",
                    "results": {"answer": random.randint(0, 5)},
                    "answerFormat": {
                        "minValue": 0,
                        "maxValue": 5,
                        "suffix": None,
                        "questionType": "Integer"
                    }
                }
            }
        }
        measurements.append({
            "sensorStartTime": int(current_time.timestamp() * 1e6),
            "data": {
                "__type": "dk.cachet.carp.who",
                "surveyResult": task_result
            }
        })
        current_time += timedelta(weeks=1)
    return measurements


# Function to generate HADS survey measurements
def generate_hads_survey_measurements(start, end):
    measurements = []
    current_time = start
    while current_time <= end:
        task_result = {
            "identifier": str(uuid.uuid4()),
            "startDate": current_time.isoformat(),
            "endDate": (current_time + timedelta(minutes=10)).isoformat(),
            "results": {
                "step1": {
                    "identifier": "step1",
                    "startDate": current_time.isoformat(),
                    "endDate": (current_time + timedelta(minutes=1)).isoformat(),
                    "questionTitle": "I feel tense or 'wound up'",
                    "results": {"answer": random.randint(0, 3)},
                    "answerFormat": {
                        "minValue": 0,
                        "maxValue": 3,
                        "suffix": None,
                        "questionType": "Integer"
                    }
                },
                "step2": {
                    "identifier": "step2",
                    "startDate": (current_time + timedelta(minutes=1)).isoformat(),
                    "endDate": (current_time + timedelta(minutes=2)).isoformat(),
                    "questionTitle": "I still enjoy the things I used to enjoy",
                    "results": {"answer": random.randint(0, 3)},
                    "answerFormat": {
                        "minValue": 0,
                        "maxValue": 3,
                        "suffix": None,
                        "questionType": "Integer"
                    }
                }
            }
        }
        measurements.append({
            "sensorStartTime": int(current_time.timestamp() * 1e6),
            "data": {
                "__type": "dk.cachet.carp.hads",
                "surveyResult": task_result
            }
        })
        current_time += timedelta(weeks=2)
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
step_count_trigger_id = 6
who5_survey_trigger_id = 8
hads_survey_trigger_id = 9

# Generate a single JSON file with the updated structure including WHO-5 and HADS survey measurements
deployment_id = generate_deployment_id()
step_count_measurements = generate_step_count_measurements(start_time, end_time)
who5_survey_measurements = generate_who5_survey_measurements(start_time, end_time)
hads_survey_measurements = generate_hads_survey_measurements(start_time, end_time)

# Randomly remove some data to create missing data
random.shuffle(step_count_measurements)
random.shuffle(who5_survey_measurements)
random.shuffle(hads_survey_measurements)

missing_step_count = random.randint(0, len(step_count_measurements) // 2)
missing_who5_survey = random.randint(0, len(who5_survey_measurements) // 2)
missing_hads_survey = random.randint(0, len(hads_survey_measurements) // 2)

step_count_measurements = step_count_measurements[missing_step_count:]
who5_survey_measurements = who5_survey_measurements[missing_who5_survey:]
hads_survey_measurements = hads_survey_measurements[missing_hads_survey:]

# Introduce invalid survey answers
invalid_survey_count = 0
for survey in who5_survey_measurements + hads_survey_measurements:
    for step in survey["data"]["surveyResult"]["results"].values():
        if random.random() < 0.1:  # 10% chance to introduce invalid answer
            if survey["data"]["__type"] == "dk.cachet.carp.who":
                step["results"]["answer"] = random.randint(6, 10)
            elif survey["data"]["__type"] == "dk.cachet.carp.hads":
                step["results"]["answer"] = random.randint(4, 6)
            invalid_survey_count += 1

file_content = [
    generate_data_stream(deployment_id, "Primary Phone", "dk.cachet.carp.stepcount", step_count_measurements,
                         step_count_trigger_id),
    generate_data_stream(deployment_id, "Survey Service", "dk.cachet.carp.who", who5_survey_measurements,
                         who5_survey_trigger_id),
    generate_data_stream(deployment_id, "Survey Service", "dk.cachet.carp.hads", hads_survey_measurements,
                         hads_survey_trigger_id)
]

# Save the JSON file
output_file_path = '/mnt/data/generated_data.json'
with open(output_file_path, 'w') as f:
    json.dump(file_content, f)
