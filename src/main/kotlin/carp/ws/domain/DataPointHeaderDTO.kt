package carp.ws.domain

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.io.Serializable

data class DataPointHeaderDto
    (
    /** The [studyId] of the request. */
    val studyId: String? = null,

    /** The [userId] of the request. */
    val userId: String? = null,

    /** The [dataFormat] of the request. */
    val dataFormat: HashMap<*, *>? = null,

    /** The [triggerId] of the request. */
    var triggerId: String? = null,

    /** The [deviceRoleName] of the request. */
    var deviceRoleName: String? = null,

    /** The [uploadTime] of the request. */
    var uploadTime: Instant = Clock.System.now(),

    /** The [startTime] of the request. */
    var startTime: Instant = Clock.System.now(),

    /** The [endTime] of the request. */
    var endTime: Instant = Clock.System.now()
) : Serializable