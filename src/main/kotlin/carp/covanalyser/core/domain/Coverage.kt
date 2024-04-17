package carp.covanalyser.core.domain

import kotlinx.datetime.Instant

/**
 * Represents this project's definition of coverage. Absolute coverage is the total number of available data points divided by the expected number of data points.
 * The time coverage wis the coverage on a time scale, it splits the data points into timeboxes based on the expectation, and compares the number of fulfilled timeboxes with the total number of timeboxes.
 */
class Coverage(val absCoverage: Double, val timeCoverage: Double, val startTime: Instant, val endTime: Instant) {
    override fun toString(): String {
        return "Coverage(absCoverage=$absCoverage, timeCoverage=$timeCoverage, startTime=$startTime, endTime=$endTime)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Coverage

        if (absCoverage != other.absCoverage) return false
        if (timeCoverage != other.timeCoverage) return false
        if (startTime != other.startTime) return false
        if (endTime != other.endTime) return false

        return true
    }

    override fun hashCode(): Int {
        var result = absCoverage.hashCode()
        result = 31 * result + timeCoverage.hashCode()
        result = 31 * result + startTime.hashCode()
        result = 31 * result + endTime.hashCode()
        return result
    }


}