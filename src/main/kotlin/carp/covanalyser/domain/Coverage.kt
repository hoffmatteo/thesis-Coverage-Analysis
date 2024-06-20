package carp.covanalyser.domain

import kotlinx.datetime.Instant

/**
 * Represents a coverage value.
 *
 * @param absCoverage The absolute coverage value, defined solely by the number of measurements.
 * @param expectationCoverage The expectation based coverage value, which takes timeboxing through expectations into account.
 * @param startTime The start time of the coverage interval.
 * @param endTime The end time of the coverage interval.
 */
data class Coverage(
    val absCoverage: Double,
    val expectationCoverage: Double,
    val startTime: Instant,
    val endTime: Instant,
)
