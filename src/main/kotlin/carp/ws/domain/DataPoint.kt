package carp.ws.domain

import kotlinx.datetime.Instant
import java.io.Serializable

data class DataPoint
    (
    /** The data point [id]. */
    var id: Int? = null,

    /** The datapoint [deploymentId]. */
    var deploymentId: String? = null,

    /** The data point [carpHeader]. */
    var carpHeader: DataPointHeaderDto? = null,

    /** The data point [carpBody]. */
    var carpBody: HashMap<*, *>? = null,

    /** The data point [storageName]. */
    var storageName: String? = null,

    /** The [createdBy] creator identity. Contains the account id of the user. */
    var createdBy: String? = null,

    /** The [updatedBy] the ID of the user the entity was updated by.
     * Contains the account id of the user. */
    var updatedBy: String? = null,

    /** The [createdAt] time of creation. */
    var createdAt: Instant? = null,

    /** The [updatedAt] last time the entity was updated. */
    var updatedAt: Instant? = null
) : Serializable


/*
[
  {
    "id": 1,
    "study_id": "c9d341ae-2209-4a70-b9a1-09446bb05dca",
    "created_by_user_id": 1,
    "carp_header": {
      "study_id": "8",
      "user_id": "user@dtu.dk",
      "data_format": {
        "name": "location",
        "namepace": "carp"
      },
      "trigger_id": "task1",
      "device_role_name": "Patient's phone",
      "upload_time": "2020-06-30T14:44:01.182Z",
      "start_time": "2018-11-08T15:30:40.721748Z",
      "end_time": "2020-06-30T14:44:01.182Z"
    },
    "carp_body": {
      "altitude": 43.3,
      "device_info": {},
      "classname": "LocationDatum",
      "latitude": 23454.345,
      "accuracy": 12.4,
      "speed_accuracy": 12.3,
      "id": "3fdd1760-bd30-11e8-e209-ef7ee8358d2f",
      "speed": 2.3,
      "longitude": 23.4,
      "timestamp": "2018-11-08T15:30:40.721748Z"
    },
    "created_at": "2020-06-30T14:44:01.251Z",
    "updated_at": "2020-06-30T14:44:01.251Z"
  }
]
 */