package carp.covanalyser.core.domain

/**
 * Represents this project's definition of coverage. Absolute coverage is the total number of available data points divided by the expected number of data points.
 * The time coverage wis the coverage on a time scale, it splits the data points into timeboxes based on the expectation, and compares the number of fulfilled timeboxes with the total number of timeboxes.
 */
class Coverage(val absCoverage: Double, val timeCoverage: Double, val timeBoxes: List<Int>) {
}