# Thesis : Framework for Coverage Analysis in Digital Phenotyping

This project is part of my DTU MSc thesis, and contains the implementation of a framework for coverage analysis in
digital phenotyping. The framework is written in Kotlin, and contains a key dependency
to [CARP Core](https://github.com/cph-cachet/carp.core-kotlin).

## Project Structure

The project is structured as a Gradle project, and contains the following modules:

- `src`: Contains the source code of the framework.
- `test`: Contains unit tests for the framework.
- `test_data`: Contains test data for the evaluation use cases.

## Architecture

The framework follows the Onion Architecture, and is thus divided into three layers:

- `Domain Layer`: Contains the core business logic of the framework.
- `Application Layer`: Contains the services of the framework.
- `Infrastructure Layer`: Contains the concrete implementations.

## Evaluation

The framework was evaluated using three use cases:

- CAMS: Coverage Analysis for CARP Mobile Sensing (CAMS).
- CATCH: Coverage Analysis for the CATCH study.
- DiaFocus: Coverage Analysis for the DiaFocus study.

These three use cases are implemented in the `TestApplication.kt` file, and can be run using the `main` function.

